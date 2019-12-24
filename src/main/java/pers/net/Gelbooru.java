package pers.net;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import pers.stor.Configuration;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.client.utils.URIBuilder;
import pers.db.H2TagManager;
import pers.db.TagManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Gelbooru extends Booru {

    private static TagManager tm;
    private static boolean tmEnabled = true;

    static {
        switch(Configuration.getDbVendor().toLowerCase()){
            case "h2":
                tm = new H2TagManager();
                try{
                    tm.createTable();
                } catch(SQLException e){
                    tmEnabled = false;
                }
                break;
            case "postgres":
                // tm = new PostgresTagManager(); //(NYI)
                break;
            default:
                tmEnabled = false;
                break;
        }
    }
    public Gelbooru(String url) throws MalformedURLException {
        super(url);
    }
    @Override
    public JsonObject postShow(String id) throws IOException {
        JsonObject post;

        URL requestURL = this.url;
        try {
            URIBuilder u = new URIBuilder(requestURL.toString());
            u.addParameter("page", "dapi");
            u.addParameter("s", "post");
            u.addParameter("q", "index");
            u.addParameter("json", "1");
            u.addParameter("id", id);
            requestURL = u.build().toURL();
        } catch(URISyntaxException e) {
            throw new MalformedURLException(requestURL.toString());
        }

        post = getJsonObject(requestURL);

        if(tmEnabled)
            tagDictGen(post);
        else
            post.addProperty("tag_string_general", post.get("tags").getAsString());

        return post;
    }

    private JsonObject getJsonObject(URL requestURL) throws IOException {
        JsonObject post;
        URLConnection r = requestURL.openConnection();
        r.setDoInput(true);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(r.getInputStream()))) {
            Gson g = new Gson();
            JsonArray a = g.fromJson(br, JsonArray.class);
            post = a.get(0).getAsJsonObject();
        }
        return post;
    }

    private void tagDictGen(JsonObject o){
        Map<String, StringBuilder> attempt = new HashMap<>();
        String tagstring = o.get("tags").getAsString();
        for(String s : tagstring.split(" ")){
            String tagName = StringEscapeUtils.unescapeXml(s);
            String type = getType(tagName);
            String key;

            switch(type.toLowerCase()){
                case "tag":
                    key = "tag_string_general";
                    break;
                case "metadata":
                    key = "tag_string_meta";
                    break;
                default:
                    key = "tag_string_"+type;
                    break;
            }
            if(!key.isEmpty()) {
                if(!attempt.containsKey(key))
                    attempt.computeIfAbsent(key, k -> {
                        StringBuilder sb = new StringBuilder();
                        sb.append(tagName).append(" ");

                        return sb;
                     });
                else
                    attempt.get(key).append(tagName).append(" ");
            }
        }

        for(Map.Entry<String, StringBuilder> k : attempt.entrySet()){
            o.addProperty(k.getKey(), k.getValue().toString());
        }

    }

    private String getType(String tagName){
        String type;

        try{
            type = tm.getType(tagName);
            if(type.equals("")){
                type = tagShow(tagName).get("type").getAsString();
                tm.insertTag(tagName, type);
            }
        } catch(SQLException e){
            tmEnabled = false;
            type = "";
        } catch(IOException e){
            type = "";
        }

        return type;
    }

    public JsonObject tagShow(String tagName) throws IOException {
        String name = StringEscapeUtils.unescapeXml(tagName.strip());

        URL requestURL = this.url;
        try {
            URIBuilder u = new URIBuilder(requestURL.toString());
            u.addParameter("page", "dapi");
            u.addParameter("s", "tag");
            u.addParameter("q", "index");
            u.addParameter("json", "1");
            u.addParameter("name", name);
            requestURL = u.build().toURL();
        } catch(URISyntaxException e) {
            throw new MalformedURLException(requestURL.toString());
        }

        return getJsonObject(requestURL);
    }
}
