/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.base;

import ca.lukegrahamlandry.lib.base.json.JsonHelper;
import ca.lukegrahamlandry.lib.network.NetworkWrapper;
import com.google.gson.*;
import net.minecraft.network.FriendlyByteBuf;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Supplier;

/**
 * Wraps an object and saves its exact class name, so it can be serialized and retain type information.
 * When serialized, any fields of the object that are subclasses of the field's type will be recreated as instances of the base class. You can get around this by nesting GenericHolders
 * @param <T> the type of object
 */
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
                Object value = ctx.deserialize(valueJson, Class.forName(className));
                return new GenericHolder<>(value);
            } catch (ClassNotFoundException e){
                throw new JsonParseException("error parsing GenericHolder. could not find class " + className);
            }
        }

        public JsonElement serialize(GenericHolder<?> obj, Type type, JsonSerializationContext ctx) {
            JsonObject out = new JsonObject();
            out.addProperty("clazz", obj.clazz.getName());
            out.add("value", ctx.serialize(obj.value));
            return out;
        }
    }

    /**
     * Writes this object to a byte buffer to be sent over the network.
     * @param buffer The existing buffer to write the object to.
     * @return The same buffer object that was passed in.
     */
    public FriendlyByteBuf encodeBytes(FriendlyByteBuf buffer) {
        JsonObject data = JsonHelper.GSON.toJsonTree(this).getAsJsonObject();
        buffer.writeUtf(data.toString(), NETWORK_MAX_CHARS);
        return buffer;
    }

    /**
     * Reads an object from a byte buffer. Constructs the same object that was passed in to encodeBytes.
     * @param buffer The buffer to read an object from.
     * @return The GenericHolder object
     */
    public static GenericHolder<?> decodeBytes(FriendlyByteBuf buffer) {
        String data = buffer.readUtf(NETWORK_MAX_CHARS);
        return JsonHelper.GSON.fromJson(data, GenericHolder.class);
    }

    /**
     * Limits the length of the encoded form of your GenericHolder when sending over the network. Defaults to the same as vanilla's default.
     * This limit applies to packets you manually send as well as automatically synced configs and data.
     * You're able to change this but if you're sending a packet bigger than 32 kb it might be a sign you should reconsider your life choices.
     */
    public static int NETWORK_MAX_CHARS = 2 << 14;
}
