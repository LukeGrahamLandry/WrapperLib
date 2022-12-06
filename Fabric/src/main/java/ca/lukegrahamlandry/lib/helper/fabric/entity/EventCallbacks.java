package ca.lukegrahamlandry.lib.helper.fabric.entity;

import ca.lukegrahamlandry.lib.base.event.IEventCallbacks;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;

public class EventCallbacks implements IEventCallbacks {
    @Override
    public void onInit() {
        ModdedSpawnEggItem.EGGS.forEach((egg) -> DispenserBlock.registerBehavior(egg, (location, stack) -> {
            Direction direction = location.getBlockState().getValue(DispenserBlock.FACING);
            EntityType<?> type = ((SpawnEggItem)stack.getItem()).getType(stack.getTag());
            try {
                type.spawn(location.getLevel(), stack, null, location.getPos().relative(direction), MobSpawnType.DISPENSER, direction != Direction.UP, false);
            }
            catch (Exception exception) {
                return ItemStack.EMPTY;
            }
            stack.shrink(1);
            location.getLevel().gameEvent(null, GameEvent.ENTITY_PLACE, location.getPos());
            return stack;
        }));
    }
}
