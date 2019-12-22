package pers.stor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dom.datatype.Post;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Serializer {

    //TODO: Config file affecting these variables
    private static String WORKDIR = "/tmp/";

    public static void save(Post p) throws IOException{
        Path path = Paths.get(WORKDIR+p.getPseudofilename()+".json");
        save(p, path);
    }

    public static void save(Post p, Path path) throws IOException {
        Gson g = new GsonBuilder().registerTypeHierarchyAdapter(byte[].class, new ByteArrayTypeAdapter()).create();
        String o = g.toJson(p);
        try {
            Files.createFile(path);
        } catch(FileAlreadyExistsException ignore){

        }

        Files.writeString(path, o);
    }
    public static Post load(Path p) throws IOException{
        Gson g = new GsonBuilder().registerTypeHierarchyAdapter(byte[].class, new ByteArrayTypeAdapter()).create();
        String s = Files.readString(p);

        return g.fromJson(s, Post.class);
    }

}
