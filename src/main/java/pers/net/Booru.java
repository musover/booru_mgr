package pers.net;

import dom.datatype.Image;
import dom.datatype.Post;
import com.google.gson.*;
import dom.datatype.Rating;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public abstract class Booru {

    protected URL url;

    public Booru(String url) throws MalformedURLException {
        this.url = new URL(url);
    }

    public abstract JsonObject postShow(String id) throws IOException;

    public Post getPost(String id) throws IOException {
        return getPost(id, true);
    }

    public Post getPost(String id, boolean hideDomain) throws IOException {
        JsonObject post = postShow(id);
        String fullPostID = hideDomain ? id : url.getHost()+"_"+id;

        URL fileURL = new URL(post.get("file_url").getAsString());

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

        switch(post.get("rating").getAsString().toLowerCase()){
            case "s":
                result.setRating(Rating.SAFE);
                break;
            case "q":
                result.setRating(Rating.QUESTIONABLE);
                break;
            case "e":
                result.setRating(Rating.EXPLICIT);
                break;
            default:
                break;
        }


        result.setImage(img);
        result.setId(fullPostID);
        result.setArtists(post.get("tag_string_artist").getAsString());
        result.setCopyright(post.get("tag_string_copyright").getAsString());
        result.setCharacters(post.get("tag_string_character").getAsString());
        result.setGeneral(post.get("tag_string_general").getAsString());
        result.setMeta(post.get("tag_string_meta").getAsString());
        result.setSource(post.get("source").getAsString());

        return result;
    }
}
