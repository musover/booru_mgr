package logic.main;

import dom.datatype.Artist;
import dom.datatype.Post;
import pers.net.Danbooru2;
import pers.stor.Configuration;
import pers.stor.datatype.ArtistStorage;
import pers.stor.datatype.PostStorage;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Danbooru2 db = (Danbooru2) Configuration.getArtistSource();
        Artist a = db.artistGet("nori_tamago");

        ArtistStorage as = ArtistStorage.getInstance();

        Danbooru2 safe = new Danbooru2("https://safebooru.donmai.us");
        Post p = safe.getPost("3726552", false);

        PostStorage ps = PostStorage.getInstance();

        ps.save(p);
        as.save(a);
    }
}
