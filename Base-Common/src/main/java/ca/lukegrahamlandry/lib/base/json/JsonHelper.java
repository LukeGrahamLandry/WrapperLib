package ca.lukegrahamlandry.lib.base.json;

import ca.lukegrahamlandry.lib.base.GenericHolder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Type;

public class JsonHelper {
    private static GsonBuilder GSON_BUILDER = new GsonBuilder().setLenient()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .registerTypeAdapter(GenericHolder.class, new GenericHolder.TypeAdapter())
            .registerTypeAdapter(CompoundTag.class, new NbtTypeAdapter())
            .registerTypeAdapter(ItemStack.class, new ItemStackTypeAdapter());

    public static Gson GSON = GSON_BUILDER.create();

    public static void addTypeAdapter(Type type, Object typeAdapter){
        GSON_BUILDER.registerTypeAdapter(type, typeAdapter);
        GSON = GSON_BUILDER.create();
    }
}
