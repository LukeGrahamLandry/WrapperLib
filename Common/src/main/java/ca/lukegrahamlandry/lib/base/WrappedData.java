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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

public abstract class WrappedData<V, S extends WrappedData<V, S>> {
    /**
     * Set the gson instance that will be used for config serialization/deserialization when interacting with files or the network.
     * This allows you to register your own type adapters. See JsonHelper for defaults provided.
     * GsonBuilder#setPrettyPrinting will automatically be called when writing defaults to a file (but not for sending over network).
     */
    public S withGson(Gson gson){
        this.gson = gson;
        return (S) this;
    }

    /**
     * The default value will always be automatically set to the parameterless constructor of T (and initialization will fail if such a constructor is not available).
     * This method allows you to call ConfigWrapper#listOf but not default to an empty list.
     */
    public S setDefaultValue(Supplier<V> v){
        this.defaultValue = v;
        return (S) this;
    }

    // API

    public Gson getGson() {
        return gson == null ? JsonHelper.get() : this.gson;
    }

    public V getDefaultValue(){
        return this.defaultValue == null ? this.reflectDefaultInstance() : this.defaultValue.get();
    }

    public Class<V> getValueClass(){
        return (Class<V>) this.valueType.getRawType();
    }

    public Logger getLogger(){
        return this.logger;
    }

    // IMPL

    protected final TypeToken<V> valueType;
    private Gson gson = null;
    private Supplier<V> defaultValue = null;
    private Logger logger;

    public WrappedData(TypeToken<V> valueType){
        this.valueType = valueType;
        this.updateLogger();
    }

    protected String getAdditionalLoggerId(){
        return null;
    }

    protected void updateLogger(){
        String id = this.getClass().getName();
        if (this.getAdditionalLoggerId() != null) id += ": " + this.getAdditionalLoggerId();
        this.logger = LoggerFactory.getLogger(id);
    }

    private V reflectDefaultInstance(){
        try {
            return (V) this.valueType.getRawType().getConstructor().newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(this.valueType.getRawType().getName() + " does not have a public parameterless constructor", e);
        }
    }
}

