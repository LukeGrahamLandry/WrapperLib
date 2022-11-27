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

import java.lang.reflect.InvocationTargetException;
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
public class DataWrapper<T> {
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
        this.synced = true;
        return (W) this;
    }

    public <W extends DataWrapper<T>> W saved(){
        this.saved = true;
        return (W) this;
    }

    public <W extends DataWrapper<T>> W named(String name){
        this.name = name;
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

    public void save(){

    }

    public void load(){

    }

    public void sync(){

    }

    public void setDirty(){

    }

    ////// CONSTRUCTION //////

    public static List<DataWrapper<?>> ALL = new ArrayList<>();
    public static MinecraftServer server;

    protected final Class<T> clazz;
    protected String name;
    protected String fileExtension;
    boolean saved = false;
    protected boolean synced = false;
    protected boolean loaded = false;
    protected final Logger logger;
    private Gson gson;

    protected DataWrapper(Class<T> clazz){
        this.clazz = clazz;
        this.name = defaultName(clazz);
        this.fileExtension = "json";
        String id = "LukeGrahamLandry/FeatureLib-Data:" + this.name;
        this.logger = LoggerFactory.getLogger(id);
        this.useGson(GSON.create());
        this.createDefaultInstance();
        ALL.add(this);
    }

    ////// IMPL //////

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
        if (canFindClass("ca.lukegrahamlandry.data.adapter.NbtTypeAdapter"))
            GSON = GSON.registerTypeAdapter(CompoundTag.class, new NbtTypeAdapter());
        if (canFindClass("ca.lukegrahamlandry.data.adapter.ItemStackTypeAdapter"))
            GSON = GSON.registerTypeAdapter(ItemStack.class, new ItemStackTypeAdapter());
    }
}
