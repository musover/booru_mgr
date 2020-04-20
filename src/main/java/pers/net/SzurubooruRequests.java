package pers.net;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dom.datatype.Image;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import javax.management.InstanceAlreadyExistsException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class SzurubooruRequests {

    private static final String AUTH_HEADER = "Authorization";

    static URL buildURL(String baseURL, String endpoint, NameValuePair...param) throws IOException {
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

    public static JsonObject postShow(URL url, String id) throws MalformedURLException, IOException {
        JsonObject post;
        BasicNameValuePair param = new BasicNameValuePair("query", "id:"+id);
        URL requestURL = buildURL(url.toString(), "/api/posts", param);

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

    public static void tagCreate(URL url, String auth, JsonObject tagBody) throws IOException, InstanceAlreadyExistsException {
        URL requestURL = buildURL(url.toString(), "/api/tags");

        try(CloseableHttpClient c = HttpClients.createDefault()){
            HttpPost r = new HttpPost(requestURL.toString());
            r.addHeader(AUTH_HEADER, auth);
            r.addHeader("Content-Type", "application/json");
            r.addHeader("Accept","application/json");

            StringEntity body = new StringEntity(tagBody.toString());
            body.setContentEncoding("UTF-8");

            r.setEntity(body);
            try(CloseableHttpResponse res = c.execute(r);
            BufferedReader br = new BufferedReader(new InputStreamReader(res.getEntity().getContent()))) {

                Gson g = new Gson();
                JsonObject response = g.fromJson(br, JsonObject.class);
                if (response.has("name") && response.get("name").getAsString().equalsIgnoreCase("TagAlreadyExistsError")) {
                    throw new InstanceAlreadyExistsException(response.get("name").getAsString());
                }
            }

        }
    }

    public static JsonObject tagCategoryShow(URL url, String type) throws IOException {
        URL requestURL = buildURL(url.toString(), "/api/tag-category/"+type);
        JsonObject res;
        URLConnection r = requestURL.openConnection();
        r.setDoInput(true);
        r.addRequestProperty("Content-Type","application/json");
        r.addRequestProperty("Accept","application/json");
        try(BufferedReader br = new BufferedReader(new InputStreamReader(r.getInputStream()))){
            Gson g = new Gson();
            res = g.fromJson(br, JsonObject.class);
        }

        return res;
    }

    public static void tagCategoryCreate(URL url, String auth, String type) throws IOException {
        URL requestURL = buildURL(url.toString(), "/api/tag-categories");

        try(CloseableHttpClient c = HttpClients.createDefault()){
            HttpPost r = new HttpPost(requestURL.toString());
            r.addHeader(AUTH_HEADER, auth);
            r.addHeader("Content-Type", "application/json");
            r.addHeader("Accept", "application/json");

            JsonObject o = new JsonObject();
            o.addProperty("name", type);
            o.addProperty("color", "#000000");

            StringEntity body = new StringEntity(o.toString());

            r.setEntity(body);
            c.execute(r);
        }

    }

    public static void postCreate(URL url, String auth, JsonObject body, Image img) throws IOException{
        URL requestURL = buildURL(url.toString(), "/api/posts");

        try(CloseableHttpClient c = HttpClients.createDefault()){
            HttpPost r = new HttpPost(requestURL.toString());
            r.addHeader(AUTH_HEADER, auth);
            r.addHeader("Accept","application/json");

            MultipartEntityBuilder mp = MultipartEntityBuilder.create();
            mp.addTextBody("metadata", body.toString(), ContentType.APPLICATION_JSON);
            mp.addBinaryBody("content", img.getImageInputStream(), ContentType.parse(img.getMimetype()), img.getPseudofilename());

            r.setEntity(mp.build());

            try(CloseableHttpResponse resp = c.execute(r);
            BufferedReader br = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()))) {
                Gson g = new Gson();
                JsonObject response = g.fromJson(br, JsonObject.class);
                if(response.has("name")){
                    throw new IOException(response.get("name").getAsString());
                }
            }

        }

    }
}
