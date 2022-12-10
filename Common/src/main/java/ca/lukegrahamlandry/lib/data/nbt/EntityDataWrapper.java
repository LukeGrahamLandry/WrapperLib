package ca.lukegrahamlandry.lib.data.nbt;

import ca.lukegrahamlandry.lib.data.NbtDataWrapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public class EntityDataWrapper<V> extends NbtDataWrapper<Entity, V> {
    CompoundTag getTag(Entity obj){
        return null;
    }

    @Override
    int getHashCode(Entity obj) {
        return obj.getUUID().hashCode();
    }
}
