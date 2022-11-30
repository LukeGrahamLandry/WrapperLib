package ca.lukegrahamlandry.lib.data.impl.map;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class LevelDataWrapper<T> extends MapDataWrapper<Level, ResourceLocation, T> {
    public LevelDataWrapper(Class<T> clazz) {
        super(ResourceLocation.class, clazz);
    }

    @Override
    public ResourceLocation keyToId(Level key) {
        return key.dimension().location();
    }

    @Override
    public ResourceLocation stringToId(String id) {
        return new ResourceLocation(id);
    }
}
