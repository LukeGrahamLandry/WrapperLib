/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.base;

import ca.lukegrahamlandry.lib.WrapperLibException;
import ca.lukegrahamlandry.lib.base.json.JsonHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.function.Supplier;

public abstract class WrappedData<V, S extends WrappedData<V, S>> {
    /**
     * Set the gson instance that will be used for config serialization/deserialization when interacting with files or the network.
     * This allows you to register your own type adapters. See JsonHelper for defaults provided.
     * GsonBuilder#setPrettyPrinting will automatically be called when writing defaults to a file (but not for sending over network).
     * <p>
     * On 1.16.5, be aware that this mutates the builder into prettyPrinting mode
     * so the same builder instance should not be passed to this method multiple times on different objects.
     * We want to have a non-pretty version because we don't want the extra spaces and new lines bloating it for sending over the network.
     * </p>
     */
    public S withGson(@NotNull GsonBuilder builder){
        this.gson = builder.create();
        this.gsonPretty = this.gson.newBuilder().setPrettyPrinting().create();
        return (S) this;
    }

    // the version of Gson shipped with 1.16.5 does not have Gson#newBuilder so this method does not exist there.
    // that's why the normal and pretty gsons are stored separately
    public S withGson(@NotNull Gson gson){
        return withGson(gson.newBuilder());
    }

    /**
     * The default value will always be automatically set to the parameterless constructor of T (and initialization will fail if such a constructor is not available).
     * This method allows you to call ConfigWrapper#listOf but not default to an empty list.
     */
    public S setDefaultValue(Supplier<@NotNull V> v){
        this.defaultValue = v;
        return (S) this;
    }

    // API

    @NotNull
    public Gson getGson() {
        return this.gson == null ? JsonHelper.get() : this.gson;
    }

    @NotNull
    public Gson getGsonPretty() {
        return this.gsonPretty == null ? JsonHelper.getPretty() : this.gsonPretty;
    }

    @NotNull
    public V getDefaultValue(){
        return this.defaultValue == null ? this.reflectDefaultInstance() : this.defaultValue.get();
    }

    @NotNull
    public Type getValueType(){
        return this.valueType.getType();
    }

    @NotNull
    public Logger getLogger(){
        return this.logger;
    }

    protected void require(boolean flag, String msg){
        if (!flag){
            this.getLogger().error(msg);
            WrapperLibException.maybeThrow(msg + " [" + this.getFullLoggerId() + "]");
        }
    }

    protected void reportError(String msg){
        this.require(false, msg);
    }

    // IMPL

    protected final TypeToken<V> valueType;
    private Gson gson = null;
    private Gson gsonPretty = null;
    private Supplier<V> defaultValue = null;
    private Logger logger;

    public WrappedData(TypeToken<V> valueType){
        this.valueType = valueType;
        this.updateLogger();
    }

    @Nullable
    protected String getAdditionalLoggerId(){
        return null;
    }

    protected void updateLogger(){
        this.logger = LoggerFactory.getLogger(this.getFullLoggerId());
    }

    protected String getFullLoggerId(){
        String id = this.getClass().getName();
        if (this.getAdditionalLoggerId() != null) id += ": " + this.getAdditionalLoggerId();
        return id;
    }

    private V reflectDefaultInstance(){
        try {
            return (V) this.valueType.getRawType().getConstructor().newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            this.reportError(this.valueType.getRawType().getName() + " does not have a public parameterless constructor");
            throw new RuntimeException("Unreachable");
        }
    }
}

