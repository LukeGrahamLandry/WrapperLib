/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.data;

import ca.lukegrahamlandry.lib.base.Available;
import ca.lukegrahamlandry.lib.base.WrappedData;
import ca.lukegrahamlandry.lib.base.json.JsonHelper;
import ca.lukegrahamlandry.lib.data.impl.GlobalDataWrapper;
import ca.lukegrahamlandry.lib.data.impl.LevelDataWrapper;
import ca.lukegrahamlandry.lib.data.impl.PlayerDataWrapper;
import com.google.gson.reflect.TypeToken;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * You can never save data that extends T with extra fields because the json won't recognise how to read it back.
 * Instead, you may use a GenericHolder which saves the exact type information.
 * Same for fields of T, they can't extend the type of the field, they must be GenericHolders as well.
 * Instead of using holders, you could write your own type adapter that saves the exact type info and call setGson.
 */
public abstract class DataWrapper<T, S extends DataWrapper<T, S>> extends WrappedData<T, S> {
    /**
     * Creates a DataWrapper tracking one object per server.
     * @param clazz the type of object to be saved. Must have a public constructor that takes no parameters to create the default value.
     */
    public static <T> GlobalDataWrapper<T> global(Class<T> clazz){
        return new GlobalDataWrapper<>(TypeToken.get(clazz));
    }

    /**
     * Creates a MapDataWrapper tracking one object per level.
     * @param clazz the type of object to be saved. Must have a public constructor that takes no parameters to create the default value.
     */
    public static <T> LevelDataWrapper<T> level(Class<T> clazz){
        return new LevelDataWrapper<>(TypeToken.get(clazz));
    }

    /**
     * Creates a MapDataWrapper tracking one object per player.
     * @param clazz the type of object to be saved. Must have a public constructor that takes no parameters to create the default value.
     */
    public static <T> PlayerDataWrapper<T> player(Class<T> clazz){
        return new PlayerDataWrapper<>(TypeToken.get(clazz));
    }

    /**
     * Mark the DataWrapper to be synced to all clients.
     */
    public S synced(){
        if (!Available.NETWORK.get()) throw new RuntimeException("Called DataWrapper#synced but WrapperLib Network module is missing.");
        this.shouldSync = true;
        return (S) this;
    }

    /**
     * Mark the DataWrapper to be saved to disk with the world.
     */
    public S saved(){
        this.shouldSave = true;
        return (S) this;
    }

    /**
     * Set the location to be used for your data file.
     * If saved, the file will be [namespace]/[path]-[side].[ext]
     */
    public S named(ResourceLocation name){
        this.dir(name.getNamespace());
        this.named(name.getPath());
        return (S) this;
    }

    /**
     * @param name the name of the DataWrapper. This will be used for the filename if saved and for matching instances when syncing.
     */
    public S named(String name){
        this.name = JsonHelper.safeFileName(name);
        this.updateLogger();
        return (S) this;
    }

    /**
     * @param subDirectory the category name of the DataWrapper. This will be used as the folder if saved and for matching instances when syncing.
     */
    public S dir(String subDirectory){
        this.subDirectory = JsonHelper.safeFileName(subDirectory);
        this.updateLogger();
        return (S) this;
    }

    /**
     * @param fileExtension the file extension to be used when writing to disk.
     */
    public S ext(String fileExtension){
        this.fileExtension = fileExtension;
        return (S) this;
    }

    // API

    /**
     * Mark the contained data as changed. This will cause it to resync to clients and save to disk when the world unloads.
     */
    public void setDirty(){
        this.isDirty = true;
        if (this.shouldSync) this.sync();
    }

    /**
     * Calling this is optional.
     * This method does nothing but serves as a reminder and semantically pleasing way to class load your data wrapper class.
     * For example, if you static init this in your data class, you must ensure it gets class loaded during your mod initialization, so the data will be loaded. So you may choose to call this method from your mod initializer.
     */
    public void init(){

    }

    // IMPL

    public static List<DataWrapper<?, ?>> ALL = new ArrayList<>();
    public static MinecraftServer server;

    private String name;
    public String fileExtension = "json";
    protected String subDirectory = null;
    boolean shouldSave = false;
    protected boolean shouldSync = false;
    protected boolean isLoaded = false;
    protected boolean isDirty = false;
    public Logger logger;

    protected DataWrapper(TypeToken<T> type){
        super(type);
        this.named(defaultName(type.getRawType()));
        ALL.add(this);
    }

    public abstract void save();

    public abstract void load();

    public abstract void sync();

    public String getName() {
        return this.name;
    }

    public String getSubDirectory() {
        return this.subDirectory;
    }

    private static String defaultName(Class<?> clazz){
        return clazz.getSimpleName().toLowerCase(Locale.ROOT);
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(DataWrapper.class);

    protected String getAdditionalLoggerId(){
        String id = "";
        if (this.getSubDirectory() != null) id = id + this.getSubDirectory() + "/";
        id += this.getName();
        return id;
    }

    public static String forDisplay(Path path){
        try {
            return path.toAbsolutePath().toFile().getCanonicalPath();
        } catch (IOException e) {
            return path.toAbsolutePath().toString();
        }
    }
}
