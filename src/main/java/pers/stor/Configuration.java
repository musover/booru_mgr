package pers.stor;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.codec.Charsets;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import pers.db.TagManager;
import pers.net.Booru;
import pers.net.IArtistSource;
import pers.net.IUploadable;
import pers.stor.typeadapters.BooruListSerializer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Configuration {

    private static String dbVendor = "h2";
    private static String dbUrl = "jdbc:h2:mem:gdpv4;DB_CLOSE_DELAY=-1";
    private static String dbUser = null;
    private static String dbPass = null;
    private static boolean dbEnabled = true;
    private static List<Booru> boards = new ArrayList<>();
    private static IArtistSource artistSource = null;
    private static boolean artistLookupEnabled = false;
    private static IUploadable uploadDestination = null;
    private static String workdir = System.getProperty("user.dir");
    private static String datadir = workdir;
    private static List<Booru> tempDisabledBoards = new ArrayList<>();

    public static void temporarilyDisableBoard(Booru b){
        boards.remove(b);
        tempDisabledBoards.add(b);
        if(b.equals(uploadDestination))
            uploadDestination = null;
        if(b.equals(artistSource))
            artistSource = null;
    }

    public static void temporarilyDisableBoards(Collection<Booru> bs){
        for(Booru b : bs)
            temporarilyDisableBoard(b);
    }

    public static boolean isDbEnabled() {
        return dbEnabled;
    }

    public static void setDbEnabled(boolean dbEnabled) {
        Configuration.dbEnabled = dbEnabled;
    }


    public static String getDatadir() {
        return datadir;
    }

    public static void setDatadir(String datadir) {
        Configuration.datadir = datadir;
    }

    public static List<String> getSupportedDbVendors(){
        return List.of("h2");
    }
    public static String getDbVendor() {
        return dbVendor;
    }

    public static void setDbVendor(String dbVendor) {
        Configuration.dbVendor = dbVendor;
    }

    public static String getDbUser() {
        return dbUser;
    }

    public static void setDbUser(String dbUser) {
        Configuration.dbUser = dbUser;
    }

    public static String getDbPass() {
        return dbPass;
    }

    public static void setDbPass(String dbPass) {
        Configuration.dbPass = dbPass;
    }

    public static IArtistSource getArtistSource() {
        return artistSource;
    }

    public static void setArtistSource(IArtistSource artistSource) {
        Configuration.artistSource = artistSource;
        artistLookupEnabled = (artistSource != null);
    }

    public static IUploadable getUploadDestination() {
        return uploadDestination;
    }

    public static void setUploadDestination(IUploadable uploadDestination) {
        Configuration.uploadDestination = uploadDestination;
    }

    public static String getDbUrl() {
        return dbUrl;
    }

    public static void setDbUrl(String dbUrl) {
        Configuration.dbUrl = dbUrl;
    }

    public static String getWorkdir() {
        return workdir;
    }

    public static void setWorkdir(String workdir) {
        Configuration.workdir = workdir;
    }

    private Configuration(){}
    static {
        try {
            load();
        } catch(FileNotFoundException|NullPointerException e) {
            try {
                save();
            } catch(IOException ex){
                Logger.getLogger(Configuration.class.getCanonicalName())
                        .log(Level.SEVERE,"Could not save default config. Bailing out.", ex);
                System.exit(420);
            }
        } catch (IOException e) {
            Logger.getLogger(Configuration.class.getCanonicalName())
                .log(Level.SEVERE, "Could not load configuration. Saving defaults.", e);

            try {
                save();
            } catch(IOException ex){
                Logger.getLogger(Configuration.class.getCanonicalName())
                        .log(Level.SEVERE,"Could not save default config. Bailing out.", ex);
                System.exit(420);
            }
        }
    }

    public static void save() throws IOException {
        Gson g = new GsonBuilder().serializeNulls().setPrettyPrinting().registerTypeAdapter(boards.getClass(), new BooruListSerializer()).create();
        Path p = Paths.get(workdir, "config.json");

        if(!boards.containsAll(tempDisabledBoards))
            boards.addAll(tempDisabledBoards);

        JsonObject configTree = new JsonObject();
        JsonElement boardJson = g.toJsonTree(boards);

        configTree.add("boards", boardJson);

        JsonObject database = new JsonObject();
        database.addProperty("vendor", dbVendor);
        database.addProperty("url", dbUrl);
        database.addProperty("user", dbUser);
        database.addProperty("pass", dbPass);

        configTree.add("database", database);
        configTree.addProperty("artistSource", (artistSource == null) ? null : artistSource.getUrl().toString());
        configTree.addProperty("uploadDestination", (uploadDestination == null) ? null : uploadDestination.getUrl().toString());
        configTree.addProperty("datadir",datadir);

        Files.writeString(p, g.toJson(configTree), Charsets.UTF_8);
    }

    public static List<Booru> getBoards() {
        return boards;
    }

    public static void addBoard(Booru b){
        boards.add(b);
    }

    public static boolean isArtistLookupEnabled() {
        return artistLookupEnabled;
    }

    public static void load() throws IOException {
        Gson g = new GsonBuilder().registerTypeAdapter(new TypeToken<List<Booru>>(){}.getType(), new BooruListSerializer()).create();
        Path p = Paths.get(workdir, "config.json");

        String s = Files.readString(p, Charsets.UTF_8);



        JsonObject configTree = g.fromJson(s, JsonObject.class);
        JsonObject database = configTree.getAsJsonObject("database");
        boards = g.fromJson(configTree.get("boards"), new TypeToken<List<Booru>>(){}.getType());

        setDbVendor(database.get("vendor").getAsString());

        setDbUrl(database.get("url").getAsString());

        if(database.get("user") != null && !database.get("user").isJsonNull())
            setDbUser(database.get("user").getAsString());
        if(database.get("pass") != null && !database.get("pass").isJsonNull())
            setDbPass(database.get("pass").getAsString());

        String artistSourceUrl = null;
        String uploadDestinationUrl = null;

        if(!configTree.get("artistSource").isJsonNull())
            artistSourceUrl = configTree.get("artistSource").getAsString();
        if(!configTree.get("uploadDestination").isJsonNull())
            uploadDestinationUrl = configTree.get("uploadDestination").getAsString();

        if(configTree.get("datadir") != null)
            setDatadir(configTree.get("datadir").getAsString());

        for(Booru b : boards){
            if(b.getUrl().toString().equals(artistSourceUrl))
                setArtistSource((IArtistSource) b);
            if(b.getUrl().toString().equals(uploadDestinationUrl))
                setUploadDestination((IUploadable) b);
        }



    }
}
