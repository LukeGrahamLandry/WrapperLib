/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.data.nbt;

import ca.lukegrahamlandry.lib.base.WrappedData;
import ca.lukegrahamlandry.lib.base.json.JsonHelper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public abstract class NbtDataWrapper<O, V, S extends NbtDataWrapper<O, V, S>> extends WrappedData<V, S> {
    public static <V> ItemStackDataWrapper<V> itemStack(Class<V> clazz){
        return new ItemStackDataWrapper<>(TypeToken.get(clazz));
    }

    public S named(String name){
        this.name = name;
        this.updateLogger();
        return (S) this;
    }

    public S named(ResourceLocation name){
        return this.named(name.toString());
    }

    // API

    public V get(O obj){
        int hashcode = this.getHashCode(obj);
        if (previousDataObjects.containsKey(hashcode)) return previousDataObjects.get(hashcode);

        V value;
        if (this.getSharedTag(obj).contains(this.getFullTagKey())) {
            String data = this.getSharedTag(obj).getString(this.getFullTagKey());
            try {
                value = this.getGson().fromJson(data, this.getValueClass());
            } catch (JsonSyntaxException e){
                this.getLogger().error("Using default. Failed to parse data: " + data);
                e.printStackTrace();
                value = this.getDefaultValue();
            }
        } else {
            value = this.getDefaultValue();
        }

        previousDataObjects.put(getHashCode(obj), value);
        return value;
    }

    public void set(O obj, V value){
        String data = this.getGson().toJson(value);
        this.getSharedTag(obj).putString(this.getFullTagKey(), data);
        previousDataObjects.put(this.getHashCode(obj), value);
    }

    public void setDirty(O obj){
        this.set(obj, previousDataObjects.get(this.getHashCode(obj)));
    }

    public void remove(O obj){
        previousDataObjects.remove(this.getHashCode(obj));
        this.getSharedTag(obj).remove(this.getFullTagKey());
    }

    // IMPL

    public static final String PARENT_TAG_KEY = "WrapperLib";
    Map<Integer, V> previousDataObjects = new HashMap<>();
    private String name;
    public NbtDataWrapper(TypeToken<V> type){
        super(type);
        this.named(type.getRawType().getSimpleName());
    }

    String getFullTagKey(){
        return this.name;
    }

    protected abstract CompoundTag getSharedTag(O obj);

    protected abstract int getHashCode(O obj);

    protected String getAdditionalLoggerId(){
        return this.name;
    }
}
