package ca.lukegrahamlandry.lib.data;

import ca.lukegrahamlandry.lib.data.adapter.ItemStackTypeAdapter;
import ca.lukegrahamlandry.lib.data.adapter.NbtTypeAdapter;
import ca.lukegrahamlandry.lib.data.type.GlobalDataWrapper;
import ca.lukegrahamlandry.lib.data.type.PlayerDataWrapper;
import ca.lukegrahamlandry.lib.data.type.LevelDataWrapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// TODO: support subdirectories

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
    }

    ////// CONSTRUCTION //////

    public static List<DataWrapper<?>> ALL = new ArrayList<>();
    public static MinecraftServer server;

    protected final Class<T> clazz;
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
        String id = "LukeGrahamLandry/FeatureLib-Data:" + this.name;
        this.logger = LoggerFactory.getLogger(id);
        this.useGson(GSON.create());
        this.createDefaultInstance();
        ALL.add(this);
    }

    ////// IMPL //////

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

    private static GsonBuilder GSON = new GsonBuilder().setLenient()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer());
    static {
        if (canFindClass("ca.lukegrahamlandry.data.adapter.NbtTypeAdapter"))
            GSON = GSON.registerTypeAdapter(CompoundTag.class, new NbtTypeAdapter());
        if (canFindClass("ca.lukegrahamlandry.data.adapter.ItemStackTypeAdapter"))
            GSON = GSON.registerTypeAdapter(ItemStack.class, new ItemStackTypeAdapter());
    }
}