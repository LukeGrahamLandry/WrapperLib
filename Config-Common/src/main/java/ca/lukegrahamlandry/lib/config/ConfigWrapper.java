package ca.lukegrahamlandry.lib.config;

import ca.lukegrahamlandry.lib.config.data.adapter.ItemStackTypeAdapter;
import ca.lukegrahamlandry.lib.config.data.adapter.NbtTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import java.util.function.Supplier;

// TODO: support subdirectories
public class ConfigWrapper<T> implements Supplier<T> {
    /**
     * Creates a new config object for reading settings from player editable files.
     * The config will be synced to all clients, so it may be used from common code.
     * The config will be loaded from world/serverconfig
     * If the file is missing we check ./config before using default values.
     */
    public static <T> ConfigWrapper<T> synced(Class<T> clazz){
        return new ConfigWrapper<>(clazz, defaultName(clazz), Side.SYNCED, "json5", true);
    }

    /**
     * Creates a new config object for reading settings from player editable files.
     * The config will ONLY be available on the logical CLIENT.
     * The config will be loaded from ./config
     */
    public static <T> ConfigWrapper<T> client(Class<T> clazz){
        return new ConfigWrapper<>(clazz, defaultName(clazz), Side.CLIENT, "json5",true);
    }

    /**
     * Creates a new config object for reading settings from player editable files.
     * The config will ONLY be available on the logical SERVER.
     * The config will be loaded from world/serverconfig
     * If the file is missing we check ./config before using default values.
     */
    public static <T> ConfigWrapper<T> server(Class<T> clazz){
        return new ConfigWrapper<>(clazz, defaultName(clazz), Side.SERVER_ONLY, "json5",true);
    }

    /**
     * Set the name to be used for your config file (and log messages).
     * The file will be [name]-[side].[ext]
     */
    public ConfigWrapper<T> named(String name){
        ALL.remove(this);
        return new ConfigWrapper<>(this.clazz, name, this.side, this.fileExtension, this.reloadable);
    }

    /**
     * Change the extension used by your config file.
     * This does NOT change the data format. It will always be stored as commented json.
     * The file will be [name]-[side].[ext]
     * Defaults to json5, other reasonable options might be json, cfg, data, config
     */
    public ConfigWrapper<T> ext(String ext){
        ALL.remove(this);
        return new ConfigWrapper<>(this.clazz, name, this.side, ext, this.reloadable);
    }

    /**
     * By default, the config may reload while the game is running.
     * If this method is called:
     * The config will be loaded once during client/server setup and then the data will never change.
     * Use this for values that are used during setup where it would be strange if they changed without fully restarting the game.
     * Calling this has the same effect as caching the object returned by ConfigWrapper#get
     */
    public ConfigWrapper<T> noReload(){
        ALL.remove(this);
        return new ConfigWrapper<>(this.clazz, this.name, this.side, this.fileExtension, false);
    }

    /**
     * Set the gson instance that will be used for config serialization/deserialization.
     * Allows you to register your own type adapters.
     * See ConfigWrapper#GSON for defaults.
     * GsonBuilder#setPrettyPrinting will automatically be called when writing defaults to a file (but not for sending over network).
     */
    public ConfigWrapper<T> useGson(Gson gson){
        this.gson = gson;
        return this;
    }

    ////// API //////

    /**
     * Retrieve the current config values as an instance of
     */
    @Override
    public T get() {
        if (!this.loaded) this.logger.debug("reading config before calling ConfigWrapper#load, default values will be used for now");
        return this.value;
    }

    /**
     * Syncs config data from the server to all clients
     * May only be called for `Side.SYNCED` configs
     */
    public void sync() {
        if (this.side != Side.SYNCED) return;
        if (!canFindClass("ca.lukegrahamlandry.lib.packets.PacketManager")){
            this.logger.error("cannot sync config to client because FeatureLib-Packets module is missing");
            return;
        }
        // TODO: send packet
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
            this.parse(reader);
            reader.close();
            this.logger.debug("config loaded from " + this.displayPath());
        } catch (IOException e) {
            this.logger.error("failed to load config from " + this.displayPath());
            e.printStackTrace();
            this.value = this.defaultConfig;
        }

        this.loaded = true;
    }

    ////// CONSTRUCTION //////

    public static MinecraftServer server;
    public static List<ConfigWrapper<?>> ALL = new ArrayList<>();
    private final T defaultConfig;
    private final String name;
    public final Side side;
    public final boolean reloadable;
    private final Class<T> clazz;
    protected T value;
    private final Logger logger;
    private boolean loaded = false;
    private final String fileExtension;
    private Gson gson;

    public ConfigWrapper(Class<T> clazz, String name, Side side, String fileExtension, boolean reloadable){
        this.clazz = clazz;
        this.name = name;
        this.side = side;
        this.fileExtension = fileExtension;
        this.reloadable = reloadable;
        String id = "LukeGrahamLandry/FeatureLib-Config:" + this.name + "-" + side.name();
        this.logger = LoggerFactory.getLogger(id);

        try {
            this.defaultConfig = clazz.getConstructor().newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            this.logger.error(clazz.getName() + " does not have a public parameterless constructor");
            throw new RuntimeException(clazz.getName() + " does not have a public parameterless constructor", e);
        }
        this.value = defaultConfig;
        this.useGson(GSON.create());
        ALL.add(this);
    }

    ////// IMPL //////

    private static String defaultName(Class<?> clazz){
        return clazz.getSimpleName().toLowerCase(Locale.ROOT).replace("config", "").replace("server", "").replace("client", "");
    }

    protected void parse(Reader reader){
        this.value = (T) this.getGson().fromJson(reader, this.clazz);
    }

    protected void writeDefaultFile() {
        if (this.side.inWorldDir){
            Path globalDefaultLocation = Paths.get("config").resolve(this.getFilename());
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
            String configData;
            if (canFindClass("ca.lukegrahamlandry.lib.config.GenerateComments")){
                configData = GenerateComments.commentedJson(this.defaultConfig, this.getGson());
            } else {
                configData = this.getGson().newBuilder().setPrettyPrinting().create().toJson(this.defaultConfig);
            }
            Files.write(this.getFilePath(), configData.getBytes());
            this.logger.debug("wrote default config to " + this.displayPath());
        } catch (IOException e){
            this.logger.error("failed to write default config to " + this.displayPath());
            e.printStackTrace();
        }
    }

    protected String getFilename(){
        return this.name.toLowerCase(Locale.ROOT) + "-" + this.side.name().toLowerCase(Locale.ROOT) + ".json5";
    }

    protected Path getFilePath(){
        switch (this.side){
            case SYNCED:
                return server.getWorldPath(LevelResource.ROOT).resolve("serverconfig").resolve(this.getFilename());
            case CLIENT:
                return Paths.get("config").resolve(this.getFilename());
            default:
                return null;
        }
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

    /**
     * Used to check if other modules are available.
     * It is safe to include only this file in your mod if you have simple config needs.
     * My extra type adapters and syncing packets will only be used if their class is found by this method.
     */
    private static boolean canFindClass(String className){
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static GsonBuilder GSON = new GsonBuilder().setLenient()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer());
    static {
        if (canFindClass("ca.lukegrahamlandry.config.data.adapter.NbtTypeAdapter"))
            GSON = GSON.registerTypeAdapter(CompoundTag.class, new NbtTypeAdapter());
        if (canFindClass("ca.lukegrahamlandry.config.data.adapter.ItemStackTypeAdapter"))
            GSON = GSON.registerTypeAdapter(ItemStack.class, new ItemStackTypeAdapter());
    }

    public enum Side {
        CLIENT(false),
        SYNCED(true),
        SERVER_ONLY(true);

        public final boolean inWorldDir;

        Side(boolean perWorld){
            this.inWorldDir = perWorld;
        }
    }
}