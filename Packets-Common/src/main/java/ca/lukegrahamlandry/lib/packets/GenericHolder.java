package ca.lukegrahamlandry.lib.packets;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;

import java.lang.reflect.Type;
import java.util.function.Supplier;

public class GenericHolder<T> implements Supplier<T> {
    public final Class<T> clazz;
    public final T value;

    public GenericHolder(T value) {
        this.value = value;
        this.clazz = (Class<T>) value.getClass();
    }

    @Override
    public T get() {
        return this.value;
    }

    public static class TypeAdapter implements JsonDeserializer<GenericHolder<?>>, JsonSerializer<GenericHolder<?>> {
        public GenericHolder<?> deserialize(JsonElement data, Type type, JsonDeserializationContext ctx) throws JsonParseException {
            String className = data.getAsJsonObject().get("clazz").getAsString();
            JsonElement valueJson = data.getAsJsonObject().get("value");
            try {
                Object value = GsonHelper.GSON.fromJson(valueJson, Class.forName(className));
                return new GenericHolder<>(value);
            } catch (ClassNotFoundException e){
                throw new JsonParseException("error parsing GenericHolder. could not find class " + className);
            }
        }

        public JsonElement serialize(GenericHolder<?> obj, Type type, JsonSerializationContext ctx) {
            JsonObject out = new JsonObject();
            out.addProperty("clazz", obj.clazz.getName());
            out.add("value", GsonHelper.GSON.toJsonTree(obj.value));
            return out;
        }
    }
}
