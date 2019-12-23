package pers.net;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dom.datatype.Artist;
import org.apache.http.client.utils.URIBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Danbooru2 extends Booru {

    private String username;
    private String apiKey;

    public Danbooru2(String url) throws MalformedURLException {
        super(url);
    }

    public Danbooru2(String url, String username, String apiKey) throws MalformedURLException {
        super(url);
        this.username = username;
        this.apiKey = apiKey;
    }

    @Override
    public JsonObject postShow(String id) throws IOException {
        JsonObject post;

        URL requestURL = new URL(this.url.toString()+"/posts/"+id+".json");
        URLConnection r = requestURL.openConnection();
        r.setDoInput(true);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(r.getInputStream()))){
            Gson g = new Gson();
            post = g.fromJson(br, JsonObject.class);
        }

        return post;
    }
    
    private JsonArray artistList(Map<String, String> param) throws IOException{
        URL requestURL;
        JsonArray a;
        try {
            URIBuilder u = new URIBuilder(this.url.toString() + "/artists.json");
            for(String k: param.keySet()){
                u.addParameter(k, param.get(k));
            }
            requestURL = u.build().toURL();
        } catch(URISyntaxException e){
            throw new MalformedURLException(e.getReason());
        }

        URLConnection r = requestURL.openConnection();
        r.setDoInput(true);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(r.getInputStream()))){
            Gson g = new Gson();
            a = g.fromJson(br, JsonArray.class);
        }

        return a;
    }

    public Artist artistGet(String name) throws IOException {
        JsonObject a = artistList(Map.of("search[name]", name)).get(0).getAsJsonObject();
        Artist out;
        List<String> urls = new ArrayList<>();
        StringBuilder otherNames = new StringBuilder();
        for(JsonElement i : a.get("urls").getAsJsonArray()){
            urls.add(i.getAsJsonObject().get("url").getAsString());
        }
        for(JsonElement i : a.get("other_names").getAsJsonArray())
            otherNames.append(i.getAsString()).append(" ");

        out = new Artist(name, urls);
        out.setGroupName(a.get("group_name").getAsString());
        out.setOtherNames(otherNames.toString());

        return out;
    }
}
