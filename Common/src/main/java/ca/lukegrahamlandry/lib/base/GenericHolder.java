/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.base;

import com.google.gson.*;

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
}
