package ca.lukegrahamlandry.lib.config;

import ca.lukegrahamlandry.lib.packets.platform.Services;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.apache.logging.log4j.core.util.FileWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

// TODO: support subdirectories
public class ConfigWrapper<T> implements Supplier<T> {
    public static MinecraftServer server;
    public static List<ConfigWrapper<?>> ALL = new ArrayList<>();
    private final T defaultConfig;
    private final String name;
    public final Side side;
    public final boolean reloadable;
    private final Class<T> clazz;
    protected T value;
    private boolean verbose;
    private Logger logger;
    private boolean loaded = false;

    public ConfigWrapper(Class<T> clazz, String name, Side side, boolean reloadable, boolean verbose){
        this.clazz = clazz;
        this.name = name;
        this.side = side;
        this.reloadable = reloadable;
        this.verbose = verbose;
        String id = this.name + "-" + side.name().toLowerCase(Locale.ROOT) + "-config";
        this.logger = LoggerFactory.getLogger(id);

        try {
            this.defaultConfig = clazz.getConstructor().newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            this.logger.error(clazz.getName() + " does not have a public parameterless constructor");
            throw new RuntimeException(clazz.getName() + " does not have a public parameterless constructor", e);
        }
        this.value = defaultConfig;

        ALL.add(this);
    }

    public ConfigWrapper(Class<T> clazz, String name, Side side){
        this(clazz, name, side, true, true);
    }

    public static <T> ConfigWrapper<T> server(Class<T> clazz){
        return new ConfigWrapper<>(clazz, clazz.getName(), Side.SERVER, true, true);
    }

    public static <T> ConfigWrapper<T> client(Class<T> clazz){
        String defaultName = clazz.getName().toLowerCase(Locale.ROOT).replace("config", "").replace("server", "").replace("client", "");
        return new ConfigWrapper<>(clazz, defaultName, Side.CLIENT, true, true);
    }

    public ConfigWrapper<T> named(String name){
        return new ConfigWrapper<>(this.clazz, name, this.side, this.reloadable, this.verbose);
    }

    ////// IMPL //////

    public void load(){
        if (this.side == Side.SERVER && server == null) {
            this.logger.error("cannot load server config before server init. default values will be used for now");
            return;
        }
        if (!Files.exists(this.getFilePath())) {
            this.writeDefaultFile();
        }

        try {
            Reader reader = Files.newBufferedReader(this.getFilePath());
            this.parse(reader);
            reader.close();
            this.logger.debug("config loaded (from " + this.getFilePath());
        } catch (IOException e) {
            this.logger.error("failed to load config from " + this.getFilename());
            e.printStackTrace();
            this.value = this.defaultConfig;
        }
    }

    public void watchFile(){
        try {
            WatchService watcher = FileSystems.getDefault().newWatchService();

            this.logger.debug("watching config file for changes: " + this.getFilePath());
        } catch (IOException e) {
            throw new RuntimeException("Couldn't watch config file", e);
        }
    }

    protected void parse(Reader reader){
        this.value = (T) this.getGson().create().fromJson(reader, this.clazz);
    }

    private void writeDefaultFile() {
        if (this.side == Side.SERVER){
            Path globalDefaultLocation = Paths.get("config").resolve(this.getFilename());
            if (Files.exists(globalDefaultLocation)){
                try {
                    Files.copy(globalDefaultLocation, this.getFilePath(), StandardCopyOption.REPLACE_EXISTING);
                    this.logger.debug("loaded global default config " + globalDefaultLocation);
                    return;
                } catch (IOException e){
                    this.logger.error("global instance config file existed but could not be copied. generating default");
                    e.printStackTrace();
                }
            }
        }

        // TODO support comments
        try {
            Files.write(this.getFilePath(), GenerateComments.commentedJson(this.defaultConfig, this.getGson()).getBytes());
        } catch (IOException e){
            this.logger.error("failed to write default config to " + this.getFilePath());
            e.printStackTrace();
        }
    }

    protected String getFilename(){
        return this.name.toLowerCase(Locale.ROOT) + "-" + this.side.name().toLowerCase(Locale.ROOT) + ".json5";
    }

    protected Path getFilePath(){
        switch (this.side){
            case SERVER:
                return server.getWorldPath(LevelResource.ROOT).resolve("serverconfig").resolve(this.getFilename());
            case CLIENT:
                return Paths.get("config").resolve(this.getFilename());
            default:
                return null;
        }
    }

    private static GsonBuilder GSON = new GsonBuilder().setLenient().registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer());
    protected GsonBuilder getGson(){
        return GSON;
    }

    @Override
    public T get() {
        return this.value;
    }

    public void sync() {
        if (this.side == Side.CLIENT) return;

        // TODO: send packet
    }

    public enum Side {
        CLIENT,
        SERVER
    }

    public static ReloadTime RELOAD_TIME = Services.PLATFORM.isDevelopmentEnvironment() ? ReloadTime.FILE_CHANGE : ReloadTime.RELOAD_COMMAND;
    public enum ReloadTime {
        FILE_CHANGE,
        RELOAD_COMMAND
    }
}
