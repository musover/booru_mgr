package pers.net;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dom.datatype.Image;
import dom.datatype.Post;
import dom.datatype.Rating;
import dom.datatype.TagType;
import org.apache.http.client.utils.URIBuilder;

import javax.management.InstanceAlreadyExistsException;
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
import java.util.logging.Logger;

public class Szurubooru extends Booru implements IUploadable{

    private transient String basicAuth = "";
    private String username;
    private String apiKey;

    public Szurubooru(){
        //I need this.
    }

    public Szurubooru(String url) throws MalformedURLException {
        super(url);
    }
    @Override
    public JsonObject postShow(String id) throws IOException {
        return SzurubooruRequests.postShow(this.url, id);
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
        //Create tags
        Map<String, List<String>> tags = p.getTags();

        for(Map.Entry<String, List<String>> e : tags.entrySet()){
            if(!e.getKey().equalsIgnoreCase(TagType.GENERAL)) {
                for (String tag : e.getValue()) {
                    tagCreate(e.getKey(), tag);
                }
            }
        }

        //Create body
        JsonObject postBody = new JsonObject();
        JsonArray t = new JsonArray();
        for(String tag: p.getTagList()){
            if(!tag.isBlank())
                t.add(tag);
        }

        postBody.add("tags", t);
        switch(p.getRating()){
            case SAFE:
                postBody.addProperty("safety","safe");
                break;
            case QUESTIONABLE:
                postBody.addProperty("safety","sketchy");
                break;
            case EXPLICIT:
                postBody.addProperty("safety", "unsafe");
                break;
        }

        if(p.getSource() != null)
            postBody.addProperty("source", p.getSource());

        SzurubooruRequests.postCreate(url, basicAuth, postBody, p.getImage());


    }

    public void tagCreate(String type, String tag) throws IOException {
        String category = (type.equalsIgnoreCase("general") ? "default": type);

        JsonObject tagBody = new JsonObject();
        JsonArray names = new JsonArray();
        names.add(tag);

        tagBody.add("names", names);
        tagBody.addProperty("category", category);

        try {
            SzurubooruRequests.tagCreate(this.url, basicAuth, tagBody);
        } catch(InstanceAlreadyExistsException e){
            Logger.getLogger(this.getClassName()).warning("Tag already exists: "+e.getMessage());
        }
    }

    public void tagCategoryShow(String type) throws IOException, NoSuchElementException {
        String name = SzurubooruRequests.tagCategoryShow(url, type).get("name").getAsString();

        if(name.equals("TagCategoryNotFoundError")){
            throw new NoSuchElementException("TagCategoryNotFoundError");
        }
    }

    public void tagCategoryCreate(String type) throws IOException {
        SzurubooruRequests.tagCategoryCreate(url, basicAuth, type);
    }

    public void createAllTagCategories() throws IOException{
        List<String> cats = Arrays.asList(TagType.META, TagType.ARTIST, TagType.CHARACTER, TagType.COPYRIGHT);
        for(String cat: cats){
            try {
                tagCategoryShow(cat);
            } catch(NoSuchElementException e) {
                tagCategoryCreate(cat);
            }
        }
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
        return apiKey;
    }

    @Override
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
        updateBasicAuth();
    }

    @Override
    public void updateBasicAuth() {
        if(username != null && apiKey != null){
            this.basicAuth = "Token " + Base64.getUrlEncoder().encodeToString((username+":"+apiKey).getBytes());
        }
    }
}
