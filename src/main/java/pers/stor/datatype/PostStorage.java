package pers.stor.datatype;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dom.datatype.Post;

import dom.datatype.Image;
import pers.stor.typeadapters.ByteArrayTypeAdapter;
import pers.stor.Configuration;
import pers.stor.typeadapters.ImageSerializer;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PostStorage implements DataStorage<Post> {

    private static PostStorage instance = null;

    private PostStorage() {

    }

    public static PostStorage getInstance(){
        if(instance == null)
            instance = new PostStorage();

        return instance;
    }
    public void save(Post p) throws IOException{
        Path path = Paths.get(Configuration.getDatadir(),"posts");
        if(!path.toFile().exists() && !path.toFile().isDirectory())
            Files.createDirectory(path);
        save(p, path);
    }

    public void save(Post p, Path path) throws IOException {
        Gson g = new GsonBuilder()
                .registerTypeHierarchyAdapter(byte[].class, new ByteArrayTypeAdapter())
                .registerTypeHierarchyAdapter(Image.class, new ImageSerializer())
                .create();
        Path actualPath = Paths.get(path.toString(), p.getId()+".json");
        String o = g.toJson(p);
        try {
            Files.createFile(actualPath);
        } catch(FileAlreadyExistsException ignore){
            //ignore
        }

        Files.writeString(actualPath, o);
    }

    @Override
    public void saveAll(List<Post> t) throws IOException {
        for(Post p : t){
            save(p);
        }
    }

    @Override
    public void saveAll(List<Post> t, Path path) throws IOException {
        for(Post p : t){
            save(p, path);
        }
    }

    public Post load(Path p) throws IOException{
        Gson g = new GsonBuilder()
                .registerTypeHierarchyAdapter(byte[].class, new ByteArrayTypeAdapter())
                .registerTypeHierarchyAdapter(Image.class, new ImageSerializer())
                .create();
        String s = Files.readString(p);

        return g.fromJson(s, Post.class);
    }

    public List<Post> loadAll(Path p) throws IOException{
        List<Post> posts = new ArrayList<>();
        if(p.toFile().isDirectory())
            for(File f : Objects.requireNonNull(p.toFile().listFiles(file -> (file != null && file.getName().endsWith(".json") && !file.getName().contains("config"))))){
                posts.add(load(f.toPath()));
            }

        return posts;
    }
}
