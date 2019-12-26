package pers.stor.datatype;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dom.datatype.Artist;
import pers.stor.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArtistStorage implements DataStorage<Artist> {

    private static ArtistStorage instance = null;

    private ArtistStorage() {

    }

    public static ArtistStorage getInstance() {
        if(instance == null)
            instance = new ArtistStorage();

        return instance;
    }
    @Override
    public void save(Artist artist) throws IOException {
        Path path = Paths.get(Configuration.getDatadir(),"artists");
        if(!path.toFile().exists() && !path.toFile().isDirectory())
            Files.createDirectory(path);

        save(artist, path);
    }

    @Override
    public void save(Artist artist, Path path) throws IOException {
        Gson g = new GsonBuilder().setPrettyPrinting().create();

        String o = g.toJson(artist);

        Path actualPath = Paths.get(path.toString(), artist.getName()+".json");

        try{
            Files.createFile(actualPath);
        } catch (FileAlreadyExistsException e){
            // ignore
        }

        Files.writeString(actualPath, o);
    }

    @Override
    public void saveAll(List<Artist> t) throws IOException {
        for(Artist a : t){
            save(a);
        }
    }

    @Override
    public void saveAll(List<Artist> t, Path path) throws IOException {
        for(Artist a : t){
            save(a, path);
        }
    }

    @Override
    public Artist load(Path p) throws IOException {
        Gson g = new Gson();

        return g.fromJson(Files.readString(p), Artist.class);
    }

    @Override
    public List<Artist> loadAll(Path p) throws IOException {
        List<Artist> artists = new ArrayList<>();
        if(p.toFile().isDirectory())
            for(File f : Objects.requireNonNull(p.toFile().listFiles(file -> (file != null && file.getName().endsWith(".json") && !file.getName().contains("config"))))){
                artists.add(load(f.toPath()));
            }

        return artists;
    }
}
