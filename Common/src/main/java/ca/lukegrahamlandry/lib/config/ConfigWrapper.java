/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.config;

import ca.lukegrahamlandry.lib.base.Available;
import ca.lukegrahamlandry.lib.base.json.JsonHelper;
import ca.lukegrahamlandry.lib.data.DataWrapper;
import ca.lukegrahamlandry.lib.network.NetworkWrapper;
import com.google.gson.Gson;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Supplier;

public class ConfigWrapper<T> implements Supplier<T> {
    /**
     * Creates a new config object for reading settings from player editable files.
     * The config will be synced to all clients, so it may be used from common code.
     * The config will be loaded from world/serverconfig
     * If the file is missing we check ./config before using default values.
     */
    public static <T> ConfigWrapper<T> synced(Class<T> clazz){
        if (!Available.NETWORK.get()) throw new RuntimeException("Called ConfigWrapper#synced but WrapperLib Network module is missing.");
        return new ConfigWrapper<>(clazz, Side.SYNCED);
    }

    /**
     * Creates a new config object for reading settings from player editable files.
     * The config will ONLY be available on the logical CLIENT.
     * The config will be loaded from ./config
     */
    public static <T> ConfigWrapper<T> client(Class<T> clazz){
        return new ConfigWrapper<>(clazz, Side.CLIENT);
    }

    /**
     * Creates a new config object for reading settings from player editable files.
     * The config will ONLY be available on the logical SERVER.
     * The config will be loaded from world/serverconfig
     * If the file is missing we check ./config before using default values.
     */
    public static <T> ConfigWrapper<T> server(Class<T> clazz){
        return new ConfigWrapper<>(clazz, Side.SERVER);
    }

    /**
     * Set the location to be used for your config file.
     * The file will be [namespace]/[path]-[side].[ext]
     */
    public ConfigWrapper<T> named(ResourceLocation name){
        this.dir(name.getNamespace());
        this.named(name.getPath());
        return this;
    }

    /**
     * Set the name to be used for your config file (and log messages).
     * The file will be [name]-[side].[ext]
     */
    public ConfigWrapper<T> named(String name){
        this.name = JsonHelper.safeFileName(name);
        this.createLogger();
        return this;
    }

    /**
     * @param subDirectory the category name of the ConfigWrapper. This will be used as the folder and for matching instances when syncing.
     */
    public ConfigWrapper<T> dir(String subDirectory){
        this.subDirectory = JsonHelper.safeFileName(subDirectory);
        this.createLogger();
        return this;
    }

    /**
     * Change the extension used by your config file.
     * This does NOT change the data format. It will always be stored as commented json.
     * The file will be [name]-[side].[ext]
     * Defaults to json5, other reasonable options might be json, cfg, data, config
     */
    public ConfigWrapper<T> ext(String fileExtension){
        this.fileExtension = fileExtension;
        return this;
    }

    /**
     * By default, the config may reload while the game is running.
     * If this method is called, the config will be loaded once during client/server setup and then the data will never change.
     * Use this for values that are used during setup where it would be strange if they changed without fully restarting the game.
     * Calling this has the same effect as caching the object returned by ConfigWrapper#get
     */
    public ConfigWrapper<T> noReload(){
        this.reloadable = false;
        return this;
    }

    /**
     * Set the gson instance that will be used for config serialization/deserialization when interacting with files or the network.
     * This allows you to register your own type adapters. See JsonHelper for defaults provided.
     * GsonBuilder#setPrettyPrinting will automatically be called when writing defaults to a file (but not for sending over network).
     */
    public ConfigWrapper<T> withGson(Gson gson){
        this.gson = gson;
        return this;
    }

    ////// API //////

    /**
     * Retrieve the current config values as an instance of
     */
    @Override
    public T get() {
        if (!this.loaded) {
            if (this.side == Side.CLIENT) this.load();
            else this.logger.debug("reading config before calling ConfigWrapper#load, default values will be used for now");
        }
        return this.value;
    }

    /**
     * Syncs config data from the server to all clients
     * May only be called for `Side.SYNCED` configs
     * Requires Packets module
     */
    public void sync() {
        if (this.side != Side.SYNCED) {
            this.logger.error("called ConfigWrapper#sync but side=" + this.side);
            return;
        }
        NetworkWrapper.sendToAllClients(new ConfigSyncMessage(this));
    }

    /**
     * Loads the config data from the file system.
     * Will automatically be called on server/client setup.
     */
    public void load(){
        if (this.side.inWorldDir && server == null) {
            this.logger.error("cannot load server config before server init. default values will be used for now");
            return;
        }
        if (!Files.exists(this.getFilePath())) this.writeDefaultFile();

        try {
            Reader reader = Files.newBufferedReader(this.getFilePath());
            this.value = this.getGson().fromJson(reader, this.clazz);
            reader.close();
            this.logger.debug("config loaded from " + this.displayPath());
        } catch (IOException e) {
            this.logger.error("failed to load config from " + this.displayPath());
            e.printStackTrace();
            this.value = this.defaultConfig;
        }

        this.loaded = true;
    }

    ////// IMPL //////

    public static MinecraftServer server;
    public static List<ConfigWrapper<?>> ALL = new ArrayList<>();
    private final T defaultConfig;
    private String name;
    public final Side side;
    private boolean reloadable;
    public final Class<T> clazz;
    protected T value;
    private Logger logger;
    private boolean loaded = false;
    private String fileExtension;
    private Gson gson;
    private String subDirectory = null;

    public ConfigWrapper(Class<T> clazz, Side side){
        this.clazz = clazz;
        this.named(defaultName(clazz));
        this.side = side;
        this.fileExtension = "json5";
        this.reloadable = false;

        try {
            this.defaultConfig = clazz.getConstructor().newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            this.logger.error(clazz.getName() + " does not have a public parameterless constructor");
            throw new RuntimeException(clazz.getName() + " does not have a public parameterless constructor", e);
        }
        this.value = defaultConfig;
        this.withGson(JsonHelper.get());
        ALL.add(this);
    }

    private static String defaultName(Class<?> clazz){
        return clazz.getSimpleName().toLowerCase(Locale.ROOT).replace("config", "").replace("server", "").replace("client", "");
    }

    void set(Object v){
        this.value = (T) v;
    }

    protected void writeDefaultFile() {
        this.getFolderPath().toFile().mkdirs();

        if (this.side.inWorldDir){
            Path globalDefaultLocation = Paths.get("config");
            if (this.subDirectory != null) globalDefaultLocation = globalDefaultLocation.resolve(this.subDirectory);
            globalDefaultLocation = globalDefaultLocation.resolve(this.getFilename());
            if (Files.exists(globalDefaultLocation)){
                try {
                    Files.copy(globalDefaultLocation, this.getFilePath(), StandardCopyOption.REPLACE_EXISTING);
                    this.logger.debug("loaded global default config " + globalDefaultLocation.toAbsolutePath().toFile().getCanonicalPath());
                    return;
                } catch (IOException e){
                    this.logger.error("global instance config file existed but could not be copied. generating default");
                    e.printStackTrace();
                }
            }
        }

        try {
            String configData = GenerateComments.commentedJson(this.defaultConfig, this.getGson());
            Files.write(this.getFilePath(), configData.getBytes());
            this.logger.debug("wrote default config to " + this.displayPath());
        } catch (IOException e){
            this.logger.error("failed to write default config to " + this.displayPath());
            e.printStackTrace();
        }
    }

    protected String getFilename(){
        return this.name + "-" + this.side.name().toLowerCase(Locale.ROOT) + "." + this.fileExtension;
    }

    private Path getFolderPath(){
        Path path;
        if (this.side.inWorldDir) path = server.getWorldPath(LevelResource.ROOT).resolve("serverconfig");
        else path = Paths.get("config");

        if (this.subDirectory != null) path = path.resolve(this.subDirectory);
        return path;
    }

    protected Path getFilePath(){
        return this.getFolderPath().resolve(this.getFilename());
    }

    /**
     * The file path to be displayed in log messages.
     */
    protected String displayPath(){
        try {
            return getFilePath().toAbsolutePath().toFile().getCanonicalPath();
        } catch (IOException e) {
            return getFilePath().toAbsolutePath().toString();
        }
    }

    public Gson getGson(){
        return this.gson;
    }

    private void createLogger(){
        String id = ConfigWrapper.class.getPackageName() + ": ";
        if (this.getSubDirectory() != null) id = id + this.getSubDirectory() + "/";
        id += this.getName() + "-" + side.name();
        this.logger = LoggerFactory.getLogger(id);
    }

    public String getName() {
        return this.name;
    }

    public String getSubDirectory() {
        return this.subDirectory;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ConfigWrapper<?>)) return false;
        ConfigWrapper<?> wrapper = (ConfigWrapper<?>) obj;
        return this.side.equals(wrapper.side) && Objects.equals(this.name, wrapper.name) && Objects.equals(this.subDirectory, wrapper.subDirectory);
    }

    public enum Side {
        CLIENT(false),
        SYNCED(true),
        SERVER(true);

        public final boolean inWorldDir;

        Side(boolean perWorld){
            this.inWorldDir = perWorld;
        }
    }
}
