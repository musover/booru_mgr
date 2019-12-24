package pers.stor;

import com.google.gson.*;
import pers.net.Booru;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BooruListSerializer implements JsonSerializer<List<Booru>>, JsonDeserializer<List<Booru>> {
    @Override
    public JsonElement serialize(List<Booru> booruList, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonArray a = new JsonArray();
        BooruSerializer bs = new BooruSerializer();
        for(Booru b: booruList){
            a.add(bs.serialize(b, type, jsonSerializationContext));
        }

        return a;
    }

    @Override
    public List<Booru> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        JsonArray a = (JsonArray) jsonElement;
        List<Booru> booruList = new ArrayList<>();
        BooruSerializer bs = new BooruSerializer();
        for(JsonElement i : a){
            booruList.add(bs.deserialize(i, type, jsonDeserializationContext));
        }

        return booruList;
    }
}
