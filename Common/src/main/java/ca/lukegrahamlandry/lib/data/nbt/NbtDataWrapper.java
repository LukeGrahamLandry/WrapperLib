/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.data.nbt;

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

public abstract class NbtDataWrapper<O, V> {
    public static <V> ItemStackDataWrapper<V> itemStack(Class<V> clazz){
        return new ItemStackDataWrapper<>(TypeToken.get(clazz));
    }

    public <W extends NbtDataWrapper<O, V>> W named(String name){
        this.name = name;
        this.createLogger();
        return (W) this;
    }

    public <W extends NbtDataWrapper<O, V>> W named(ResourceLocation name){
        return this.named(name.toString());
    }

    public <W extends NbtDataWrapper<O, V>> W withGson(Gson gson){
        this.gson = gson;
        return (W) this;
    }

    // API

    public V get(O obj){
        int hashcode = getHashCode(obj);
        if (previousDataObjects.containsKey(hashcode)) return previousDataObjects.get(hashcode);

        V value;
        if (getSharedTag(obj).contains(getFullTagKey())) {
            String data = getSharedTag(obj).getString(getFullTagKey());
            try {
                value = getGson().fromJson(data, valueType.getType());
            } catch (JsonSyntaxException e){
                this.logger.error("Using default. Failed to parse data: " + data);
                e.printStackTrace();
                value = getDefaultInstance();
            }
        } else {
            value = getDefaultInstance();
        }

        previousDataObjects.put(getHashCode(obj), value);
        return value;
    }

    public void set(O obj, V value){
        String data = getGson().toJson(value);
        getSharedTag(obj).putString(getFullTagKey(), data);
        previousDataObjects.put(getHashCode(obj), value);
    }

    public void setDirty(O obj){
        set(obj, previousDataObjects.get(getHashCode(obj)));
    }

    public void remove(O obj){
        previousDataObjects.remove(getHashCode(obj));
        getSharedTag(obj).remove(getFullTagKey());
    }

    // IMPL

    public static final String PARENT_TAG_KEY = "WrapperLib";
    Map<Integer, V> previousDataObjects = new HashMap<>();
    protected TypeToken<V> valueType;
    protected Logger logger; // TODO
    private String name;
    private Gson gson;
    public NbtDataWrapper(TypeToken<V> valueType){
        this.valueType = valueType;
        this.named(valueType.getRawType().getSimpleName());
    }

    String getFullTagKey(){
        return this.name;
    }

    private V getDefaultInstance(){
        try {
            return (V) this.valueType.getRawType().getConstructor().newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            this.logger.error(this.valueType.getRawType().getName() + " does not have a public parameterless constructor");
            throw new RuntimeException(this.valueType.getRawType().getName() + " does not have a public parameterless constructor", e);
        }
    }

    protected abstract CompoundTag getSharedTag(O obj);

    protected abstract int getHashCode(O obj);

    public Gson getGson(){
        return this.gson == null ? JsonHelper.get() : this.gson;
    }

    protected void createLogger(){
        String id = this.getClass().getName() + ": " + this.name;
        this.logger = LoggerFactory.getLogger(id);
    }
}
