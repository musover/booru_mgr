package logic.main;

import dom.datatype.Post;
import pers.net.Danbooru2;
import pers.stor.Configuration;

import javax.naming.NoPermissionException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, NoPermissionException {
        Danbooru2 safebooru = (Danbooru2) Configuration.getBoards().stream().filter(b -> b.getUrl().getHost().contains("safebooru")).findFirst().get();

        Post p = safebooru.getPost("3726590");

        Configuration.getUploadDestination().postCreate(p);

    }
}
