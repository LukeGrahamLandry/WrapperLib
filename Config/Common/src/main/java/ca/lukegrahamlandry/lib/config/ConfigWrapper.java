package ca.lukegrahamlandry.lib.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.io.Reader;
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
    private final Supplier<T> defaultConfig;
    private final String name;
    public final Side side;
    public final boolean reloadable;
    protected T value;
    private boolean verbose;

    public ConfigWrapper(Supplier<T> defaultConfig, String name, Side side, boolean reloadable, boolean verbose){
        this.defaultConfig = defaultConfig;
        this.name = name;
        this.side = side;
        this.reloadable = reloadable;
        this.value = defaultConfig.get();
        this.verbose = verbose;
        ALL.add(this);
    }

    public ConfigWrapper(Supplier<T> defaultConfig, String name, Side side){
        this(defaultConfig, name, side, true, true);
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
            this.log( "config loaded: \n" + GSON.toJsonTree(this.value).toString());
        } catch (IOException e) {
            this.log("failed to load config from " + this.getFilename());
            e.printStackTrace();
            this.value = this.defaultConfig.get();
        }
    }

    protected void parse(Reader reader){
        this.value = (T) this.getGson().fromJson(reader, defaultConfig.get().getClass());
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
            Files.write(this.getFilePath(), GSON.toJsonTree(this.defaultConfig.get()).toString().getBytes());
        } catch (IOException e){
            this.log("failed to write default config to " + this.getFilePath());
            e.printStackTrace();
        }
    }

    protected String getFilename(){
        return this.name.toLowerCase(Locale.ROOT) + "-" + this.side.name().toLowerCase(Locale.ROOT) + ".json";
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

    private static Gson GSON = new GsonBuilder().setLenient().registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer()).create();
    protected Gson getGson(){
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

    enum Side {
        CLIENT,
        SERVER
    }
}
