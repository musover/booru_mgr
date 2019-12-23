package pers.net;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

/**
 * Contains all the low-level HTTP requests with minimal JSON parsing.
 */
public class Danbooru2Requests {

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
            r.addRequestProperty("Authorization",auth);
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
     * Performs a query with one or more possible parameters
     * @param param limit: posts per page (max 200)
     *              page: page to look up
     *              tags: tags to search for
     *              md5: hash of the post's image
     *              random: random order
     *              raw: no idea of what it does
     * @return
     */
    public static JsonArray postList(String baseURL, NameValuePair ...param) throws IOException{
        return postList(baseURL, "", param);
    }

    public static JsonArray postList(String baseURL, String auth, NameValuePair ...param) throws IOException {
        URL requestURL = buildURL(baseURL, "/posts.json", param);
        return getJsonElements(requestURL, auth);
    }


}
