package pers.net;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dom.datatype.Artist;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class Danbooru2 extends Booru {

    private String basicAuth;

    public Danbooru2(String url) throws MalformedURLException {
        super(url);
    }

    public Danbooru2(String url, String username, String apiKey) throws MalformedURLException {
        super(url);
        this.basicAuth = "Basic "+ Base64.getUrlEncoder().encodeToString((username+":"+apiKey).getBytes());
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

    /**
     *
     * @param name Name of the artist (supposedly unique)
     * @return An Artist object with the first result of the query
     * @throws IOException Any exception thrown by Danbooru2.artistList()
     */
    public Artist artistGet(String name) throws IOException {
        BasicNameValuePair param = new BasicNameValuePair("search[name]", name);
        JsonObject a = Danbooru2Requests.listArtists(this.url.toString(), param).get(0).getAsJsonObject();
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



    public List<String> postListIds(Map<String, String> nvp) throws IOException {
        List<NameValuePair> paramList = new ArrayList<>();
        for(String k : nvp.keySet()){
            paramList.add(new BasicNameValuePair(k, nvp.get(k)));
        }
        NameValuePair[] paramArray = paramList.toArray(NameValuePair[]::new);
        JsonArray a;
        List<String> result = new ArrayList<>();
        if(nvp.get("tags").split(" ").length > 2)
            a = Danbooru2Requests.postList(this.url.toString(), this.basicAuth, paramArray);
        else
            a = Danbooru2Requests.postList(this.url.toString(), paramArray);

        for(JsonElement i : a){
            result.add(i.getAsJsonObject().get("id").getAsString());
        }

        return result;
    }
}
