package ca.lukegrahamlandry.lib.packets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class GsonHelper {
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

    public static Gson GSON;
    private static GsonBuilder GSON_BUILDER = new GsonBuilder().setLenient()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .registerTypeAdapter(GenericHolder.class, new GenericHolder.TypeAdapter());

    // TODO: better handling of people adding adapters
    static {
        Map<Class<?>, String> adapters = new HashMap<>();
        adapters.put(CompoundTag.class, "ca.lukegrahamlandry.lib.config.data.adapter.NbtTypeAdapter");
        adapters.put(ItemStack.class, "ca.lukegrahamlandry.lib.config.data.adapter.ItemStackTypeAdapter");

        for (Map.Entry<Class<?>, String> adapter : adapters.entrySet()){
            if (canFindClass(adapter.getValue())){
                try {
                    GSON_BUILDER = GSON_BUILDER.registerTypeAdapter(adapter.getKey(), Class.forName(adapter.getValue()).getConstructor().newInstance());
                } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ignored){}
            }
        }

        GSON = GSON_BUILDER.create();
    }
}
