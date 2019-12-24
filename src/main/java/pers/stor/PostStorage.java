package pers.stor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dom.datatype.Post;

import dom.datatype.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PostStorage {

    private PostStorage(){
    }

    public static void save(Post p) throws IOException{
        Path path = Paths.get(Configuration.getWorkdir(),p.getId()+".json");
        save(p, path);
    }

    public static void save(Post p, Path path) throws IOException {
        Gson g = new GsonBuilder()
                .registerTypeHierarchyAdapter(byte[].class, new ByteArrayTypeAdapter())
                .registerTypeHierarchyAdapter(Image.class, new ImageSerializer())
                .create();
        String o = g.toJson(p);
        try {
            Files.createFile(path);
        } catch(FileAlreadyExistsException ignore){
            //ignore
        }

        Files.writeString(path, o);
    }

    public static Post load(Path p) throws IOException{
        Gson g = new GsonBuilder()
                .registerTypeHierarchyAdapter(byte[].class, new ByteArrayTypeAdapter())
                .registerTypeHierarchyAdapter(Image.class, new ImageSerializer())
                .create();
        String s = Files.readString(p);

        return g.fromJson(s, Post.class);
    }

    public static List<Post> loadAll(Path p) throws IOException{
        List<Post> posts = new ArrayList<>();
        if(p.toFile().isDirectory())
            for(File f : Objects.requireNonNull(p.toFile().listFiles(file -> (file != null && file.getName().endsWith(".json") && !file.getName().contains("config"))))){
                posts.add(load(f.toPath()));
            }

        return posts;
    }

}
