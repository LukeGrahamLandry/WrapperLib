package ca.lukegrahamlandry.lib.base.json;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegistryObjectTypeAdapterFactory implements TypeAdapterFactory {
    public static Map<Class<?>, Adapter<?>> adapters = new HashMap<>();

    public static void add(Class<?> clazz, Registry<?> registry){
        adapters.put(clazz, new Adapter<>(registry));
    }

    static {
        add(Item.class, Registry.ITEM);
        add(Block.class, Registry.BLOCK);
        add(EntityType.class, Registry.ENTITY_TYPE);
        add(BlockEntityType.class, Registry.BLOCK_ENTITY_TYPE);
        add(Enchantment.class, Registry.ENCHANTMENT);
        add(MobEffect.class, Registry.MOB_EFFECT);
        add(Fluid.class, Registry.FLUID);
        add(Potion.class, Registry.POTION);
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        for (Map.Entry<Class<?>, Adapter<?>> entry : adapters.entrySet()){
            if (entry.getKey().isAssignableFrom(type.getRawType())) return (TypeAdapter<T>) entry.getValue();
        }

        return null;
    }

    private static class Adapter<T> extends TypeAdapter<T> {
        private final Registry<T> registry;

        public Adapter(Registry<T> registry){
            this.registry = registry;
        }
        @Override
        public void write(JsonWriter out, T value) throws IOException {
            ResourceLocation rl = this.registry.getKey(value);
            out.value(rl.toString());
        }

        @Override
        public T read(JsonReader in) throws IOException {
            String rl = in.nextString();
            return this.registry.get(new ResourceLocation(rl));
        }
    }
}
