package pers.stor.typeadapters;

import com.google.gson.*;
import pers.net.Booru;
import pers.net.IUploadable;

import java.lang.reflect.Type;

public class BooruSerializer implements JsonSerializer<Booru>, JsonDeserializer<Booru> {
    @Override
    public Booru deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        JsonObject o = jsonElement.getAsJsonObject();
        String className = o.get("class").getAsString();
        Booru b;
        try{
            Class<?> booruClass = Class.forName(className);
            b = jsonDeserializationContext.deserialize(jsonElement, booruClass);
            if(b instanceof IUploadable){
                ((IUploadable) b).updateBasicAuth();
            }
        } catch (ClassNotFoundException e){
            throw new JsonParseException(e);
        }
        return b;
    }

    @Override
    public JsonElement serialize(Booru booru, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonElement e = jsonSerializationContext.serialize(booru, Booru.class);
        e.getAsJsonObject().addProperty("class", booru.getClass().getCanonicalName());
        if(booru instanceof IUploadable)
        {
            IUploadable db = (IUploadable) booru;
            if(db.getApiKey() != null && db.getUsername() != null){
                e.getAsJsonObject().addProperty("username", db.getUsername());
                e.getAsJsonObject().addProperty("apiKey", db.getApiKey());
            }
        }

        return e;
    }
}
