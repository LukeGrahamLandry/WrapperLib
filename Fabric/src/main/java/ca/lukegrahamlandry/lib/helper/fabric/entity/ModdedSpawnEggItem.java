package ca.lukegrahamlandry.lib.helper.fabric.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModdedSpawnEggItem extends SpawnEggItem {
    public static List<ModdedSpawnEggItem> EGGS = new ArrayList<>();
    private final Supplier<EntityType<? extends Mob>> realType;

    public ModdedSpawnEggItem(Supplier<EntityType<? extends Mob>> type, int colourA, int colourB, Properties props) {
        super(null, colourA, colourB, props);
        this.realType = type;
        EGGS.add(this);
    }

    @Override
    public EntityType<?> getType(@Nullable CompoundTag compoundTag) {
        EntityType<?> type = super.getType(compoundTag);
        return type != null ? type : this.realType.get();
    }
}
