package pers.net;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dom.datatype.Artist;
import dom.datatype.Post;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import javax.naming.NoPermissionException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class Danbooru2 extends Booru implements IUploadable, IArtistUploadable, IArtistSource {

    private transient String basicAuth = "";
    private String username;
    private String apiKey;

    public Danbooru2(String url) throws MalformedURLException {
        super(url);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        updateBasicAuth();
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
        updateBasicAuth();
    }

    public void updateBasicAuth(){
        if(username != null && apiKey != null)
            this.basicAuth = "Basic "+ Base64.getUrlEncoder().encodeToString((username+":"+apiKey).getBytes());
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

    /**
     *
     * @param name Name of the artist (supposedly unique)
     * @return An Artist object with the first result of the query
     * @throws IOException Any exception thrown by Danbooru2.artistList()
     */
    public Artist artistGet(String name) throws IOException {
        BasicNameValuePair param = new BasicNameValuePair("search[name]", name);
        //（ ´∀｀）＜ ぬるぽ
        JsonArray arr = Danbooru2Requests.listArtists(this.url.toString(), param);
        JsonObject a;
        if(arr.size() >= 1)
            a = arr.get(0).getAsJsonObject();
        else
            return null;
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



    public List<String> postListIds(Map<String, String> nvp) throws IOException, NoPermissionException {
        List<NameValuePair> paramList = new ArrayList<>();
        for(Map.Entry<String, String> e: nvp.entrySet()){
            paramList.add(new BasicNameValuePair(e.getKey(), e.getValue()));
        }
        NameValuePair[] paramArray = paramList.toArray(NameValuePair[]::new);
        JsonArray a;
        List<String> result = new ArrayList<>();
        if(nvp.get("tags").split(" ").length > 2) {
            if(basicAuth == null || this.basicAuth.isEmpty())
                throw new NoPermissionException("You are not authenticated.");
            a = Danbooru2Requests.postList(this.url.toString(), this.basicAuth, paramArray);
        } else
            a = Danbooru2Requests.postList(this.url.toString(), paramArray);

        for(JsonElement i : a){
            result.add(i.getAsJsonObject().get("id").getAsString());
        }

        return result;
    }

    @Override
    public void postCreate(Post p) throws IOException, NoPermissionException {
        List<NameValuePair> param = new ArrayList<>();
        param.add(new BasicNameValuePair("upload[rating]", String.valueOf(p.getRating().toString().toLowerCase().charAt(0))));
        param.add(new BasicNameValuePair("upload[tag_string]", p.getTagstring()));
        if(!p.getParent().isEmpty())
            param.add(new BasicNameValuePair("upload[parent_id]", p.getParent()));

        if(basicAuth == null || this.basicAuth.isEmpty())
            throw new NoPermissionException("You are not authenticated.");

        String newPost = Danbooru2Requests.postCreate(
                this.url.toString(),
                this.basicAuth,
                p.getImage(),
                param.toArray(NameValuePair[]::new));

        Danbooru2Requests.postUpdate(
                this.url.toString(),
                this.basicAuth,
                newPost,
                new BasicNameValuePair("post[source]", p.getSource()));
    }

    @Override
    public void artistCreate(Artist a) throws IOException, NoPermissionException {
        List<NameValuePair> param = new ArrayList<>();
        param.add(new BasicNameValuePair("artist[name]", a.getName()));
        if(!a.getUrlString().isEmpty())
            param.add(new BasicNameValuePair("artist[url_string]", a.getUrlString()));
        if(!a.getGroupName().isEmpty())
            param.add(new BasicNameValuePair("artist[group_name]", a.getGroupName()));
        if(!a.getOtherNames().isEmpty())
            param.add(new BasicNameValuePair("artist[other_names]", a.getOtherNames()));

        if(basicAuth == null || this.basicAuth.isEmpty())
            throw new NoPermissionException("You are not authenticated.");

        Danbooru2Requests.artistCreate(url.toString(), basicAuth, param.toArray(NameValuePair[]::new));
    }

    @Override
    public String toString() {
        return "Danbooru2{" +
                "basicAuth='" + basicAuth + '\'' +
                ", username='" + username + '\'' +
                ", apiKey='" + apiKey + '\'' +
                ", url=" + url +
                '}';
    }
}
