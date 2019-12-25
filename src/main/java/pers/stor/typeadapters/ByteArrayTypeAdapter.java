package pers.stor.typeadapters;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Base64;

/**
 * If left to its own devices, Gson will, by default, encode it all as the string representation of byte[].
 * Base64 encoding is not perfect, but at least it only has an overhead of 33%. Trials of yEnc were unsuccessful.
 */
public class ByteArrayTypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
    @Override
    public byte[] deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        return Base64.getUrlDecoder().decode(jsonElement.getAsString());
    }

    @Override
    public JsonElement serialize(byte[] bytes, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(Base64.getUrlEncoder().encodeToString(bytes));
    }
}
