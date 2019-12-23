package logic.main;

import pers.net.Danbooru2;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {

        Danbooru2 d = new Danbooru2("https://danbooru.donmai.us");
        List<String> ids = d.postListIds(Map.of("tags", "konpaku_youmu"));
        System.out.println(ids);
    }
}
