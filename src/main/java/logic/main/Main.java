package logic.main;

import dom.datatype.Post;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import pers.net.Danbooru2;
import pers.net.Danbooru2Requests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {

        Danbooru2 d = new Danbooru2("https://danbooru.donmai.us");
        List<String> ids = d.postListIds(Map.of("tags", "konpaku_youmu"));
        System.out.println(ids);

        Post p = d.getPost(ids.get(2));

        List<NameValuePair> nvpList = new ArrayList<>();
        nvpList.add(new BasicNameValuePair("upload[rating]",String.valueOf(p.getRating().toString().toLowerCase().charAt(0))));
        nvpList.add(new BasicNameValuePair("upload[tag_string]", p.getTagstring()));
        String s = Danbooru2Requests.postCreate("http://fuckshit:3000",
                "Basic "+ Base64.getUrlEncoder().encodeToString(("musover:yuvMymzqQGUWCRrFyit7J924").getBytes()),
                p.getImage(),nvpList.toArray(NameValuePair[]::new));

        System.out.println(s);
    }
}
