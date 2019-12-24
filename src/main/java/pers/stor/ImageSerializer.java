package pers.stor;

import com.google.gson.*;
import dom.datatype.Image;

import java.io.IOException;
import java.lang.reflect.Type;

public class ImageSerializer implements JsonDeserializer<Image>, JsonSerializer<Image> {
    @Override
    public Image deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject o = jsonElement.getAsJsonObject();
        Image i = new Image();
        i.setId(o.get("id").getAsString());
        i.setMimetype(o.get("mimetype").getAsString());
        try {
            i.setFile(new ByteArrayTypeAdapter().deserialize(o.get("file"), type, jsonDeserializationContext));
        } catch (IOException e) {
            throw new JsonParseException(e);
        }

        return i;
    }

    @Override
    public JsonElement serialize(Image image, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject tree = new JsonObject();
        tree.addProperty("id", image.getId());
        tree.addProperty("mimetype", image.getMimetype());
        try {
            tree.add("file", new ByteArrayTypeAdapter().serialize(image.getFile(), type, jsonSerializationContext));
        } catch(IOException e) {
            throw new JsonParseException(e);
        }

        return tree;
    }
}
