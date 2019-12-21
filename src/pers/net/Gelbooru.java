package pers.net;

import com.google.gson.JsonObject;

import java.net.MalformedURLException;

public class Gelbooru extends Booru {

    public Gelbooru(String url) throws MalformedURLException {
        super(url);
    }
    @Override
    public JsonObject postShow(String id) {
        return null;
    }
}
