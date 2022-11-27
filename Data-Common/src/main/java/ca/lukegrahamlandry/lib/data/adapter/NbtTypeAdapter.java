package ca.lukegrahamlandry.lib.data.adapter;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;

import java.lang.reflect.Type;

public class NbtTypeAdapter implements JsonDeserializer<CompoundTag>, JsonSerializer<CompoundTag> {
    public NbtTypeAdapter() {
    }

    public CompoundTag deserialize(JsonElement data, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        try {
            return TagParser.parseTag(data.getAsString());
        } catch (CommandSyntaxException e) {
            throw new JsonParseException(e);
        }
    }

    public JsonElement serialize(CompoundTag obj, Type type, JsonSerializationContext ctx) {
        return new JsonPrimitive(obj.getAsString());
    }
}