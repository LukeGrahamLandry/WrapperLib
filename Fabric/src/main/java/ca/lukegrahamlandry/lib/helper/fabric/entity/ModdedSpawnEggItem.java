package ca.lukegrahamlandry.lib.helper.fabric.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModdedSpawnEggItem extends SpawnEggItem {
    private final Supplier<EntityType<? extends Mob>> realType;

    public ModdedSpawnEggItem(Supplier<EntityType<? extends Mob>> type, int backgroundColor, int highlightColor, Properties props) {
        super(null, backgroundColor, highlightColor, props);
        this.realType = type;
        DispenserBlock.registerBehavior(this, ModdedSpawnEggItem::dispense);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) ColorProviderRegistry.ITEM.register((stack, i) -> this.getColor(i), this);
    }

    @Override
    public EntityType<?> getType(@Nullable CompoundTag compoundTag) {
        EntityType<?> type = super.getType(compoundTag);
        return type != null ? type : this.realType.get();
    }

    private static ItemStack dispense(BlockSource location, ItemStack stack) {
        Direction direction = location.getBlockState().getValue(DispenserBlock.FACING);
        EntityType<?> type = ((SpawnEggItem) stack.getItem()).getType(stack.getTag());
        try {
            type.spawn(location.getLevel(), stack, null, location.getPos().relative(direction), MobSpawnType.DISPENSER, direction != Direction.UP, false);
        } catch (Exception exception) {
            return ItemStack.EMPTY;
        }
        stack.shrink(1);
        location.getLevel().gameEvent(null, GameEvent.ENTITY_PLACE, location.getPos());
        return stack;
    }


}
