package org.figuramc.fsb.api.json;

import com.google.gson.*;
import org.figuramc.fsb.api.avatars.EHashPair;
import org.figuramc.fsb.api.utils.Hash;

import java.lang.reflect.Type;

public class EHashPairSerializer implements JsonSerializer<EHashPair>, JsonDeserializer<EHashPair> {
    @Override
    public EHashPair deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement instanceof JsonArray && ((JsonArray) jsonElement).size() == 2) {
            JsonArray array = (JsonArray) jsonElement;
            Hash hash = jsonDeserializationContext.deserialize(array.get(0), Hash.class);
            Hash ehash = jsonDeserializationContext.deserialize(array.get(1), Hash.class);
            return new EHashPair(hash, ehash);
        }
        throw new JsonParseException("EHash pair has to be represented in 2 strings array");
    }

    @Override
    public JsonElement serialize(EHashPair eHashPair, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonArray array = new JsonArray();
        array.add(eHashPair.hash().toString());
        array.add(eHashPair.ehash().toString());
        return array;
    }
}
