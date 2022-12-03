/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.network;

import ca.lukegrahamlandry.lib.base.json.JsonHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.network.FriendlyByteBuf;

import java.util.*;

import static ca.lukegrahamlandry.lib.base.GenericHolder.NETWORK_MAX_CHARS;

/**
 * WIP. unused
 */
public class EfficientNetworkSerializer {
    // must call .enableComplexMapKeySerialization() so keys of hash maps arent lost. then they're treated as arrays of [key, value] pairs
    private static void recurseEncode(FriendlyByteBuf buffer, JsonElement data){
        if (data.isJsonObject()) {
            JsonObject obj = data.getAsJsonObject();
            List<String> keys = new ArrayList<>(obj.keySet());
            buffer.writeInt(keys.size());
            Collections.sort(keys);
            for (String key : keys){
                recurseEncode(buffer, obj.get(key));
            }
        } else if (data.isJsonArray()) {
            JsonArray array = data.getAsJsonArray();
            buffer.writeInt(array.size());
            for (JsonElement entry : array){
                recurseEncode(buffer, entry);
            }
        } else if (data.isJsonPrimitive()) {

        }else {
            if (((JsonPrimitive) data).isString()){
                buffer.writeUtf(data.getAsString(), NETWORK_MAX_CHARS);
            } else {
                buffer.writeUtf(data.toString(), NETWORK_MAX_CHARS);
            }
        }
    }


    // really i want to get json out of this so i can run it back through the type adapters
    private static JsonElement recurseDecode(FriendlyByteBuf buffer, Class<?> clazz){
        // 1. hashmap
        // 2. array or list or set
        // 3. primitive or string
        // 4. object with fields
        if (clazz == String.class || clazz.isPrimitive()){
            return JsonHelper.get().fromJson(buffer.readUtf(), JsonElement.class);
        }
        if (clazz.isArray()){
            int length = buffer.readInt();
            for (int i=0;i<length;i++){
                String data = buffer.readUtf(NETWORK_MAX_CHARS);

            }
        }

        if (Collection.class.isAssignableFrom(clazz)){
            int length = buffer.readInt();
            for (int i=0;i<length;i++){
                String data = buffer.readUtf(NETWORK_MAX_CHARS);

            }
        }

        if (Map.class.isAssignableFrom(clazz)){
            // it will be an array of pairs: [key, value]
            int length = buffer.readInt();
            for (int i=0;i<length;i++){
                int two = buffer.readInt();
//                JsonElement key = recurseDecode();
//                JsonElement value = recurseDecode();
            }
        }


        return null;
    }
}
