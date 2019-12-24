package pers.net;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import dom.datatype.Image;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Arrays;

/**
 * Contains all the low-level HTTP requests with minimal JSON parsing.
 */
public class Danbooru2Requests {

    private static final String AUTH_HEADER = "Authorization";

    private Danbooru2Requests(){}

    static URL buildURL(String baseURL, String endpoint, NameValuePair ...param) throws IOException {
        URL requestURL;
        try {
            URIBuilder u = new URIBuilder(baseURL + endpoint);
            u.addParameters(Arrays.asList(param));
            requestURL = u.build().toURL();
        } catch(URISyntaxException e){
            throw new MalformedURLException(e.getReason());
        }

        return requestURL;
    }

    static JsonArray getJsonElements(URL requestURL) throws IOException {
        return getJsonElements(requestURL, "");
    }

    static JsonArray getJsonElements(URL requestURL, String auth) throws IOException {
        JsonArray a;
        URLConnection r = requestURL.openConnection();
        if(!auth.isEmpty())
            r.addRequestProperty(AUTH_HEADER,auth);
        r.setDoInput(true);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(r.getInputStream()))){
            Gson g = new Gson();
            a = g.fromJson(br, JsonArray.class);
        }

        return a;
    }

    /**
     * Issues a GET request for artist lookup
     * @param param Any or none of the following
     *              search[name] : returns a list of artists for a given name
     *              search[id] : ID lookup
     *              search[group_name]
     *              search[any_name_matches]
     *              search[url_matches]
     *              search[creator_name]
     *              search[creator_id]
     *              search[is_active]
     *              search[is_banned]
     *              search[order]
     * @return A JsonArray with the result of the query as-is
     * @throws IOException MalformedURLException or other network errors
     */
    public static JsonArray listArtists(String baseURL, NameValuePair ...param) throws IOException {

        URL requestURL = buildURL(baseURL,"/artists.json", param);
        return getJsonElements(requestURL);
    }

    /**
     * Performs a query with one or more possible parameters, with no possibility for HTTP-Basic auth.
     * @param param limit: posts per page (max 200)
     *              page: page to look up
     *              tags: tags to search for
     *              md5: hash of the post's image
     *              random: random order
     *              raw: no idea of what it does
     * @return A JsonArray with the result of the query.
     */
    public static JsonArray postList(String baseURL, NameValuePair ...param) throws IOException{
        return postList(baseURL, "", param);
    }

    public static JsonArray postList(String baseURL, String auth, NameValuePair ...param) throws IOException {
        URL requestURL = buildURL(baseURL, "/posts.json", param);
        return getJsonElements(requestURL, auth);
    }

    /**
     * Issues a POST request to upload a post.
     * @param baseURL Danbooru2 object's url
     * @param auth Danbooru2 object's basicAuth
     * @param img Image, which will get uploaded as a multipart form
     * @param param upload[source] - Should not be used if upload[file] is not null
     *              upload[rating] - s|q|e (REQUIRED)
     *              upload[parent_id]
     *              upload[tag_string] REQUIRED
     * @return The ID of the newly created post
     * @throws IOException .
     */
    public static String postCreate(String baseURL, String auth, Image img, NameValuePair ...param) throws IOException {
        URL requestURL = buildURL(baseURL, "/uploads.json");

        HttpPost r = new HttpPost(requestURL.toString());
        r.addHeader(AUTH_HEADER,auth);
        MultipartEntityBuilder b = MultipartEntityBuilder.create();
        for(NameValuePair p : param) {
            b.addTextBody(p.getName(), p.getValue());
        }
        if(img != null)
            b.addBinaryBody("upload[file]",img.getFile(),ContentType.create(img.getMimetype()),img.getPseudofilename());

        HttpEntity multipart = b.build();
        r.setEntity(multipart);

        JsonObject o;
        try(CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse resp = httpClient.execute(r)){

            Gson g = new Gson();
            o = g.fromJson(new InputStreamReader(resp.getEntity().getContent()), JsonObject.class);

        }

        if(o.get("post_id") instanceof JsonNull)
            throw new IOException(o.get("backtrace").getAsString());

        return o.get("post_id").getAsString();

    }

    /**
     * Issues a PUT request to update a post.
     * @param baseURL Danbooru2 object's url
     * @param auth Danbooru2 object's basicAuth
     * @param id ID of the post
     * @param param post[source] - Should not be used if upload[file] is not null
     *              post[rating] - s|q|e (REQUIRED)
     *              post[parent_id]
     *              post[tag_string] REQUIRED
     * @throws IOException .
     */
    public static void postUpdate(String baseURL, String auth, String id, NameValuePair ...param) throws IOException{
        URL requestURL = buildURL(baseURL, "/posts/"+id+".json");

        try(CloseableHttpClient c = HttpClients.createDefault()){
            HttpPut r = new HttpPut(requestURL.toString());
            r.addHeader(AUTH_HEADER,auth);
            EntityBuilder b = EntityBuilder.create();
            b.setParameters(param);

            HttpEntity e = b.build();
            r.setEntity(e);
            c.execute(r);
        }
    }


}
