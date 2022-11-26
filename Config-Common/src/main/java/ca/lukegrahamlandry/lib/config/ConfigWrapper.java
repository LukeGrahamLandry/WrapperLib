package ca.lukegrahamlandry.lib.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
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
public class ConfigWrapper<T> implements Supplier<T>{
    public static MinecraftServer server;
    public static List<ConfigWrapper<?>> ALL = new ArrayList<>();
    private final T defaultConfig;
    private final String name;
    public final Side side;
    public final boolean reloadable;
    private final Class<T> clazz;
    protected T value;
    private boolean verbose;

    public ConfigWrapper(Class<T> clazz, String name, Side side, boolean reloadable, boolean verbose){
        this.clazz = clazz;
        try {
            this.defaultConfig = clazz.getConstructor().newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        this.name = name;
        this.side = side;
        this.reloadable = reloadable;
        this.value = defaultConfig;
        this.verbose = verbose;
        ALL.add(this);
    }

    public ConfigWrapper(Class<T> clazz, String name, Side side){
        this(clazz, name, side, true, true);
    }

    public void load(){
        if (this.side == Side.SERVER && server == null) {
            this.log("cannot load server config before server init. default values will be used for now");
            return;
        }
        if (!Files.exists(this.getFilePath())) {
            this.writeDefaultFile();
        }

        try {
            Reader reader = Files.newBufferedReader(this.getFilePath());
            this.parse(reader);
            reader.close();
            this.log( "config loaded (" + this.getFilePath() + "): \n" + this.getGson().create().toJsonTree(this.value).toString());
        } catch (IOException e) {
            this.log("failed to load config from " + this.getFilename());
            e.printStackTrace();
            this.value = this.defaultConfig;
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
                    this.log("loaded global default config " + globalDefaultLocation);
                    return;
                } catch (IOException e){
                    this.log("global instance config file existed but could not be copied. generating default");
                    e.printStackTrace();
                }
            }
        }

        // TODO support comments
        try {
            Files.write(this.getFilePath(), GenerateComments.commentedJson(this.defaultConfig, this.getGson()).getBytes());
        } catch (IOException e){
            this.log("failed to write default config to " + this.getFilePath());
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

    protected void log(String s){
        if (this.verbose) System.out.println("(" + this.name + " " + this.side + ") " + s);
    }

    public void sync() {
        if (this.side == Side.CLIENT) return;

        // TODO: send packet
    }

    public enum Side {
        CLIENT,
        SERVER
    }
}
