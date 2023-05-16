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
import ca.lukegrahamlandry.lib.base.InternalUseOnly;
import ca.lukegrahamlandry.lib.base.WrappedData;
import ca.lukegrahamlandry.lib.base.json.JsonHelper;
import com.google.gson.reflect.TypeToken;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.function.Supplier;

public class ConfigWrapper<T> extends WrappedData<T, ConfigWrapper<T>> implements Supplier<T> {
    /**
     * Creates a new config object for reading settings from player editable files.
     * The config will be synced to all clients, so it may be used from common code.
     * The config will be loaded from world/serverconfig
     * If the file is missing we check ./defaultconfigs then ./config before using default values.
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
     * If the file is missing we check ./defaultconfigs then ./config before using default values.
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
        this.updateLogger();
        return this;
    }

    /**
     * @param subDirectory the category name of the ConfigWrapper. This will be used as the folder and for matching instances when syncing.
     */
    public ConfigWrapper<T> dir(String subDirectory){
        this.subDirectory = JsonHelper.safeFileName(subDirectory);
        this.updateLogger();
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
        this.shouldReload = false;
        return this;
    }

    /**
     * Instead of reading one of the specified object from the config file, read a list of them.
     * @param <L> {@code List<T>}
     */
    public <L extends List<T>> ConfigWrapper<L> listOf(){
        ALL.remove(this);
        TypeToken<L> type = (TypeToken<L>) TypeToken.getParameterized(ArrayList.class, this.getValueType());
        ConfigWrapper<L> newWrapper = new ConfigWrapper<>(type, this.side);
        return newWrapper.withSettings(this);
    }

    /**
     * Instead of reading one of the specified object from the config file, read a map of strings to objects.
     * @param <M> {@code Map<String, T>}
     */
    public <M extends Map<String, T>> ConfigWrapper<M> mapOf(){
        return mapOf(String.class);
    }

    /**
     * Instead of reading one of the specified object from the config file, read a map.
     * @param <K> the type for the keys of the map. This must have a toString method that preforms the json serialization.
     *            The normal type adapter will not be called unless you use Gson#enableComplexMapSerialization (set a Gson instance with ConfigWrapper#withGson).
     *            Examples that work: numbers, String, UUID, ResourceLocation
     * @param <M> {@code Map<K, T>}
     */
    public <K, M extends Map<K, T>> ConfigWrapper<M> mapOf(Class<K> keyClass){
        ALL.remove(this);
        TypeToken<M> type = (TypeToken<M>) TypeToken.getParameterized(HashMap.class, keyClass, this.getValueType());
        ConfigWrapper<M> newWrapper = new ConfigWrapper<>(type, this.side);
        return newWrapper.withSettings(this);
    }

    public ConfigWrapper<T> onLoad(Runnable action){
        this.onLoadAction = action;
        return this;
    }

    // API

    /**
     * Retrieve the current config values as an instance of T.
     * Will be null for server/synced configs if called before loaded.
     */
    @Override
    public T get() {
        if (this.value == null) {
            if (this.side == Side.CLIENT) this.load();
            else this.getLogger().error("Cannot read server/synced config before the server starts or after it stops (or synced config from client before player receives sync packet)");
        }
        return this.value;
    }

    /**
     * Calling this is optional.
     * This method does nothing but serves as a reminder and semantically pleasing way to class load your config wrapper class.
     * For example, if you static init this in your config data class, you must ensure it gets class loaded during your mod initialization, so the config file will be loaded. So you may choose to call this method from your mod initializer.
     */
    public void init(){

    }

    // IMPL

    /**
     * Syncs config data from the server to all clients
     * May only be called for `Side.SYNCED` configs
     * Requires Packets module
     */
    public void sync() {
        if (this.side != Side.SYNCED) this.getLogger().error("called ConfigWrapper#sync but side=" + this.side + ". Ignoring.");
        else new ConfigSyncMessage(this).sendToAllClients();
    }

    /**
     * Loads the config data from the file system.
     * Will automatically be called on server/client setup.
     */
    public void load(){
        if (this.side.inWorldDir && server == null) {
            this.getLogger().error("cannot load server config before server init. default values will be used for now");
            return;
        }
        if (!Files.exists(this.getFilePath())) this.writeDefaultFile();

        try {
            Reader reader = Files.newBufferedReader(this.getFilePath());
            this.value = this.getGson().fromJson(reader, this.getValueType());
            reader.close();
            this.getLogger().info("config loaded from " + this.displayPath());
        } catch (IOException e) {
            this.getLogger().error("failed to load config from " + this.displayPath());
            e.printStackTrace();
        }

        this.onLoadAction.run();
    }

    public static MinecraftServer server;
    public static List<ConfigWrapper<?>> ALL = new ArrayList<>();
    private String name;
    public final Side side;
    public boolean shouldReload = true;
    protected T value = null;
    private String fileExtension;
    private String subDirectory = null;
    private Runnable onLoadAction = () -> {};

    private ConfigWrapper(Class<T> clazz, Side side){
        this(TypeToken.get(clazz), side);
        this.named(defaultName(clazz));
    }

    public ConfigWrapper(TypeToken<T> type, Side side){
        super(type);
        this.side = side;
        this.fileExtension = "json5";
        ALL.add(this);
        this.named(type.toString());
    }

    private static String defaultName(Class<?> clazz){
        return clazz.getSimpleName().toLowerCase(Locale.ROOT).replace("config", "").replace("server", "").replace("client", "");
    }

    @InternalUseOnly
    void set(Object v){
        this.value = (T) v;
        this.loaded = true;
    }

    public boolean isLoaded(){
        return this.loaded;
    }

    public static List<Path> defaultConfigFolders = Arrays.asList(Paths.get("defaultconfigs"), Paths.get("config"));

    protected void writeDefaultFile() {
        this.getFolderPath().toFile().mkdirs();

        if (this.side.inWorldDir){
            for (Path defaultLocation : defaultConfigFolders){
                if (this.subDirectory != null) defaultLocation = defaultLocation.resolve(this.subDirectory);
                defaultLocation = defaultLocation.resolve(this.getFilename());
                if (Files.exists(defaultLocation)){
                    try {
                        Files.copy(defaultLocation, this.getFilePath(), StandardCopyOption.REPLACE_EXISTING);
                        this.getLogger().info("found global default config " + defaultLocation.toAbsolutePath().toFile().getCanonicalPath());
                        return;
                    } catch (IOException e){
                        this.getLogger().error("global instance config file existed but could not be copied. generating default");
                        e.printStackTrace();
                    }
                }
            }
        }

        try {
            String configData = GenerateComments.commentedJson(this.getDefaultValue(), this.getGsonPretty());
            Files.write(this.getFilePath(), configData.getBytes());
            this.getLogger().info("wrote default config to " + this.displayPath());
        } catch (IOException e){
            this.getLogger().error("failed to write default config to " + this.displayPath());
            e.printStackTrace();
        }
    }

    protected String getFilename(){
        return this.name + "-" + this.side.name().toLowerCase(Locale.ROOT) + "." + this.fileExtension;
    }

    protected Path getFolderPath(){
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

    @Override
    protected String getAdditionalLoggerId() {
        String id = "";
        if (this.getSubDirectory() != null) id = id + this.getSubDirectory() + "/";
        id += this.getName() + "-" + side;
        return id;
    }

    public String getName() {
        return this.name;
    }

    public String getSubDirectory() {
        return this.subDirectory;
    }

    private ConfigWrapper<T> withSettings(ConfigWrapper<?> other){
        this.named(other.getName());
        if (other.getSubDirectory() != null) this.dir(other.getSubDirectory());
        this.ext(other.fileExtension);
        this.withGson(other.getGson());
        this.shouldReload = other.shouldReload;
        if (other.valueType.equals(this.valueType)) this.setDefaultValue(() -> (T) other.getDefaultValue());
        return this;
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
