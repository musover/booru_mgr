package logic.main;


import dom.datatype.Post;
import pers.stor.Configuration;
import pers.stor.Serializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        Configuration.load();

        List<Post> postList = Serializer.loadAll(Paths.get(Configuration.getWorkdir()));
        for(Post p: postList){
            Files.write(Paths.get(Configuration.getWorkdir(), p.getImage().getPseudofilename()), p.getImage().getFile());
        }
    }
}
