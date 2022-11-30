/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.data;

import ca.lukegrahamlandry.lib.base.json.JsonHelper;
import ca.lukegrahamlandry.lib.data.impl.GlobalDataWrapper;
import ca.lukegrahamlandry.lib.data.impl.map.LevelDataWrapper;
import ca.lukegrahamlandry.lib.data.impl.map.PlayerDataWrapper;
import com.google.gson.Gson;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
public abstract class DataWrapper<T> {

    public static <T> GlobalDataWrapper<T> global(Class<T> clazz){
        return new GlobalDataWrapper<>(clazz);
    }

    public static <T> LevelDataWrapper<T> level(Class<T> clazz){
        return new LevelDataWrapper<>(clazz);
    }

    public static <T> PlayerDataWrapper<T> player(Class<T> clazz){
        return new PlayerDataWrapper<>(clazz);
    }

    public <W extends DataWrapper<T>> W synced(){
        this.shouldSync = true;
        return (W) this;
    }

    public <W extends DataWrapper<T>> W saved(){
        this.shouldSave = true;
        return (W) this;
    }

    public <W extends DataWrapper<T>> W named(String name){
        this.name = name.toLowerCase(Locale.ROOT).replace(":", "-").replace(" ", "-");
        return (W) this;
    }

    public <W extends DataWrapper<T>> W dir(String subDirectory){
        this.subDirectory = subDirectory;
        return (W) this;
    }

    public <W extends DataWrapper<T>> W ext(String fileExtension){
        this.fileExtension = fileExtension;
        return (W) this;
    }

    public <W extends DataWrapper<T>> W useGson(Gson gson){
        this.gson = gson;
        return (W) this;
    }

    ////// API //////

    public abstract void save();

    public abstract void load();

    public abstract void sync();

    public void setDirty(){
        this.isDirty = true;
        if (this.shouldSync) this.sync();
    }

    ////// CONSTRUCTION //////

    public static List<DataWrapper<?>> ALL = new ArrayList<>();
    public static MinecraftServer server;

    public final Class<T> clazz;
    protected String name;
    protected String fileExtension = "json";
    protected String subDirectory = null;
    boolean shouldSave = false;
    protected boolean shouldSync = false;
    protected boolean isLoaded = false;
    protected boolean isDirty = false;
    protected final Logger logger;
    private Gson gson;

    protected DataWrapper(Class<T> clazz){
        this.clazz = clazz;
        this.named(defaultName(clazz));
        String id = "LukeGrahamLandry/WrapperLib Data:" + this.name;
        this.logger = LoggerFactory.getLogger(id);
        this.useGson(JsonHelper.GSON);
        this.createDefaultInstance();
        ALL.add(this);
    }

    ////// IMPL //////

    public String getName() {
        return this.name;
    }

    public String getSubDirectory() {
        return this.subDirectory;
    }

    protected abstract Path getFilePath();

    private static String defaultName(Class<?> clazz){
        return clazz.getSimpleName().toLowerCase(Locale.ROOT);
    }

    protected T createDefaultInstance() {
        try {
            return clazz.getConstructor().newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            this.logger.error(clazz.getName() + " does not have a public parameterless constructor");
            throw new RuntimeException(clazz.getName() + " does not have a public parameterless constructor", e);
        }
    }

    public Gson getGson(){
        return this.gson;
    }

    /**
     * Used to check if other modules are available.
     * It is safe to include only this file in your mod if you have simple needs.
     * My extra type adapters and syncing packets will only be used if their class is found by this method.
     */
    protected static boolean canFindClass(String className){
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    protected static String forDisplay(Path path){
        try {
            return path.toAbsolutePath().toFile().getCanonicalPath();
        } catch (IOException e) {
            return path.toAbsolutePath().toString();
        }
    }
}
