package pers.net;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dom.datatype.TagType;
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

    static {
        switch(Configuration.getDbVendor().toLowerCase()){
            case "h2":
                tm = new H2TagManager();
                try{
                    tm.createTable();
                } catch(SQLException e){
                    Configuration.setDbEnabled(false);
                }
                break;
            case "postgres":
                // tm = new PostgresTagManager(); //(NYI)
                break;
            default:
                Configuration.setDbEnabled(false);
                break;
        }
    }

    public static synchronized void disableTm(){
        Configuration.setDbEnabled(false);
    }
    public Gelbooru(){
        super();
        //I need this.
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

        post = getJsonObject(requestURL, "post");

        if(post == null)
            return null;

        if(Configuration.isDbEnabled())
            tagDictGen(post);
        else
            post.addProperty("tag_string_general", post.get("tags").getAsString());

        return post;
    }

    private JsonObject getJsonObject(URL requestURL, String object) throws IOException {
        JsonObject post;
        URLConnection r = requestURL.openConnection();
        r.setDoInput(true);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(r.getInputStream()))) {
            Gson g = new Gson();
            JsonObject o = g.fromJson(br, JsonObject.class);
            JsonArray a = o.getAsJsonArray(object);
            if(!a.isJsonNull())
                post = a.get(0).getAsJsonObject();
            else
                post = null;
        }
        return post;
    }

    private void tagDictGen(JsonObject o){
        Map<String, StringBuilder> attempt = new HashMap<>();
        String tagstring = o.get("tags").getAsString();
        for(String s : tagstring.split(" ")) {
            String tagName = StringEscapeUtils.unescapeXml(s);
            String type = getType(tagName);
            String key;

            switch (type.toLowerCase()) {
                case "":
                case "tag":
                    key = "tag_string_general";
                    break;
                case "metadata":
                    key = "tag_string_meta";
                    break;
                default:
                    key = "tag_string_" + type;
                    break;
            }
            if (!attempt.containsKey(key)) {
                attempt.computeIfAbsent(key, k -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append(tagName).append(" ");

                    return sb;
                });
            } else {
                attempt.get(key).append(tagName).append(" ");
            }

            for (Map.Entry<String, StringBuilder> k : attempt.entrySet()) {
                o.addProperty(k.getKey(), k.getValue().toString());
            }

        }

    }

    private String getType(String tagName){
        String type;

        try{
            type = tm.getType(tagName);
            if(type.equals("")){
                type = intToStrTagType(tagShow(tagName).get("type").getAsInt());
                tm.insertTag(tagName, type);
            }
        } catch(SQLException e){
            disableTm();
            type = "";
        } catch(IOException|NullPointerException e){
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

        return getJsonObject(requestURL, "tag");
    }

    private static String intToStrTagType(int tt){
        switch(tt){
            default:
            case 0:
                return TagType.GENERAL;
            case 1:
                return TagType.ARTIST;
            case 3:
                return TagType.COPYRIGHT;
            case 4:
                return TagType.CHARACTER;
            case 5:
                return TagType.META;
        }
    }
}
