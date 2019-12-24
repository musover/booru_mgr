package pers.stor;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.codec.Charsets;
import pers.net.Booru;
import pers.net.Danbooru2;
import pers.net.Gelbooru;
import pers.net.Uploadable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Configuration {

    private static String dbVendor = "h2";
    private static String dbUrl = "jdbc:h2:mem:gdpv4;DB_CLOSE_DELAY=-1";
    private static String dbUser = null;
    private static String dbPass = null;
    private static List<Booru> boards = new ArrayList<>();
    private static Booru artistSource = null;
    private static boolean artistLookupEnabled = false;
    private static Uploadable uploadDestination = null;

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

    public static Booru getArtistSource() {
        return artistSource;
    }

    public static void setArtistSource(Booru artistSource) {
        Configuration.artistSource = artistSource;
        if(artistSource != null)
            artistLookupEnabled = true;
    }

    public static Uploadable getUploadDestination() {
        return uploadDestination;
    }

    public static void setUploadDestination(Uploadable uploadDestination) {
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

    private static String workdir = System.getProperty("user.dir");
    private Configuration(){}
    static {
        try {
            load();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                save();
            } catch(IOException ex){
                e.printStackTrace();
                System.exit(420);
            }
        }
    }

    public static void save() throws IOException {
        Gson g = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(boards.getClass(), new BooruListSerializer()).create();
        Path p = Paths.get(workdir, "config.json");

        JsonObject configTree = new JsonObject();
        JsonElement boardJson = g.toJsonTree(boards);

        configTree.add("boards", boardJson);

        JsonObject database = new JsonObject();
        database.addProperty("vendor", dbVendor);
        database.addProperty("url", dbUrl);
        database.addProperty("user", dbUser);
        database.addProperty("pass", dbPass);

        configTree.add("database", database);
        configTree.addProperty("artistSource", artistSource.getUrl().toString());
        configTree.addProperty("uploadDestination", uploadDestination.getUrl().toString());

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

        if(database.get("user") != null && database.get("user").isJsonNull())
            setDbUser(database.get("user").getAsString());
        if(database.get("pass") != null && !database.get("pass").isJsonNull())
            setDbPass(database.get("pass").getAsString());

        String artistSourceUrl = configTree.get("artistSource").getAsString();
        String uploadDestinationUrl = configTree.get("uploadDestination").getAsString();
        for(Booru b : boards){
            if(b.getUrl().toString().equals(artistSourceUrl))
                setArtistSource(b);
            if(b.getUrl().toString().equals(uploadDestinationUrl))
                setUploadDestination((Uploadable) b);
        }



    }
}
