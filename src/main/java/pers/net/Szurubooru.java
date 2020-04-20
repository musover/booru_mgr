package pers.net;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dom.datatype.Image;
import dom.datatype.Post;
import dom.datatype.Rating;
import org.apache.http.client.utils.URIBuilder;

import javax.naming.NoPermissionException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class Szurubooru extends Booru implements IUploadable{

    private transient String basicAuth = "";
    private String username;
    private String token;

    public Szurubooru(){
        //I need this.
    }

    public Szurubooru(String url) throws MalformedURLException {
        super(url);
    }
    @Override
    public JsonObject postShow(String id) throws IOException {
        JsonObject post;

        URL requestURL = this.url;

        try {
            URIBuilder u = new URIBuilder(requestURL.toString()+"/api/posts");
            u.addParameter("query","id:"+id);
            requestURL = u.build().toURL();
        } catch(URISyntaxException e){
            throw new MalformedURLException(requestURL.toString());
        }

        URLConnection r = requestURL.openConnection();
        r.setDoInput(true);
        r.addRequestProperty("Content-Type","application/json");
        r.addRequestProperty("Accept","application/json");

        try(BufferedReader br = new BufferedReader(new InputStreamReader(r.getInputStream()))){
            Gson g = new Gson();
            post = g.fromJson(br, JsonObject.class);
        }

        post = post.get("results").getAsJsonArray().get(0).getAsJsonObject();

        return post;
    }

    @Override
    public Post getPost(String id, boolean hideDomain) throws IOException{
        JsonObject post = postShow(id);
        String fullPostID = hideDomain ? id : url.getHost()+"_"+id;

        URL fileURL = new URL(this.url.toString()+post.get("contentUrl").getAsString());

        URLConnection ir = fileURL.openConnection();
        ir.setDoInput(true);
        String mimetype = ir.getContentType();

        Image img;
        try(BufferedInputStream bis = new BufferedInputStream(ir.getInputStream())) {
            img = new Image();
            img.setId(fullPostID);
            img.setFile(bis.readAllBytes());
            img.setMimetype(mimetype);
        }

        Post result = new Post();

        switch(post.get("safety").getAsString().toLowerCase()){
            case "safe":
                result.setRating(Rating.SAFE);
                break;
            case "sketchy":
                result.setRating(Rating.QUESTIONABLE);
                break;
            case "unsafe":
                result.setRating(Rating.EXPLICIT);
                break;
            default:
                break;
        }

        JsonArray tagArr = post.get("tags").getAsJsonArray();

        Map<String, List<String>> tags = new HashMap<>();

        for(JsonElement tag: tagArr){
            JsonObject tjo = tag.getAsJsonObject();
            String value = tjo.get("names").getAsJsonArray().get(0).getAsString();
            String category = tjo.get("category").getAsString();
            String filteredCat = (category.equalsIgnoreCase("default") ? "general" : category);

            List<String> dest = tags.putIfAbsent(filteredCat, new ArrayList<>());
            if(dest == null)
                dest = tags.get(filteredCat);

            dest.add(value);
        }

        result.setTags(tags);

        result.setImage(img);
        result.setId(fullPostID);

        result.setSource(post.get("source").getAsString());

        return result;
    }

    @Override
    public void postCreate(Post p) throws IOException, NoPermissionException {

    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
        updateBasicAuth();
    }

    @Override
    public String getApiKey() {
        return token;
    }

    @Override
    public void setApiKey(String apiKey) {
        this.token = apiKey;
        updateBasicAuth();
    }

    @Override
    public void updateBasicAuth() {
        if(username != null && token != null){
            this.basicAuth = "Token " + Base64.getUrlEncoder().encodeToString((username+":"+token).getBytes());
        }
    }
}
