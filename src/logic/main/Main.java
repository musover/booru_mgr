package logic.main;

import com.google.gson.Gson;
import dom.datatype.Artist;
import dom.datatype.Post;
import pers.net.Danbooru2;
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
        //Post p = Serializer.load(Paths.get("/tmp/5051590.png.cbf"));
        //System.out.println(p.getCharacters());

        Danbooru2 d = new Danbooru2("https://danbooru.donmai.us");
        /*Post p = d.getPost("3724595",false);
        Path path = Paths.get("/tmp/"+p.getPseudofilename());
        //Files.createFile(path);
        Files.write(path, p.getImageFile());
        Serializer.save(p);*/

        Artist a =  d.artistGet("nori_tamago");
        Gson g = new Gson();
        System.out.println(g.toJson(a));


    }
}
