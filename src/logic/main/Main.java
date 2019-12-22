package logic.main;

import dom.datatype.Post;
import pers.net.Gelbooru;
import pers.stor.Serializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        //Gelbooru g = new Gelbooru("https://gelbooru.com");
        //Post p = g.getPost("5051590");
        //Path path = Paths.get("/tmp/"+p.getPseudofilename());
        //Files.createFile(path);
        //Files.write(path,p.getImageFile());
        //Serializer.save(p);
        Post p = Serializer.load(Paths.get("/tmp/5051590.png.cbf"));
        System.out.println(p.getCharacters());
    }
}
