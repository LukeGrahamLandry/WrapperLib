package ca.lukegrahamlandry.lib.data.nbt;

import ca.lukegrahamlandry.lib.data.NbtDataWrapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class ItemStackDataWrapper<V> extends NbtDataWrapper<ItemStack, V> {
    CompoundTag getTag(ItemStack obj){
        if (!obj.hasTag()) obj.setTag(obj.getOrCreateTag());
        return obj.getTag();
    }

    @Override
    int getHashCode(ItemStack obj) {

        return 0;
    }
}
