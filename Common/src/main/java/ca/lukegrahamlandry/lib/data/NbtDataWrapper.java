/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.data;

import ca.lukegrahamlandry.lib.base.WorkInProgress;
import ca.lukegrahamlandry.lib.base.json.JsonHelper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.minecraft.nbt.CompoundTag;
import org.slf4j.Logger;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@WorkInProgress
public abstract class NbtDataWrapper<O, V> {
    Map<Integer, V> lastDataObjects = new HashMap<>();

    String getFullTagKey(){
        return "";
    }

    public V get(O obj){
        int hashcode = getHashCode(obj);
        if (lastDataObjects.containsKey(hashcode)) return lastDataObjects.get(hashcode);

        if (!getTag(obj).contains(getFullTagKey())) {

        }

        String data = getTag(obj).getString(getFullTagKey());
        try {
            V value = getGson().fromJson(data, actualType);
            lastDataObjects.put(getHashCode(obj), value);
            return value;
        } catch (JsonSyntaxException e){
            this.logger.error("Failed to parse data: " + data);
            e.printStackTrace();
            return null;
        }
    }

    public void set(O obj, V value){
        String data = getGson().toJson(value);
        getTag(obj).putString(getFullTagKey(), data);
        lastDataObjects.put(getHashCode(obj), value);
    }

    public void setDirty(O obj){
        set(obj, lastDataObjects.get(getHashCode(obj)));
    }

    protected abstract CompoundTag getTag(O obj);

    protected abstract int getHashCode(O obj);

    Type actualType;
    Logger logger;
    Gson getGson(){
        return JsonHelper.get();
    }
}
