package ca.lukegrahamlandry.lib.entity.forge;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class EntityHelperImpl {
    public static final List<AttributeContainer> attributes = new ArrayList<>();
    public static void attributes(EntityType<? extends LivingEntity> type, AttributeSupplier.Builder builder) {
        attributes.add(new AttributeContainer(type, builder));
    }

    @SubscribeEvent
    public static void handleAttributeEvent(EntityAttributeCreationEvent event){
        attributes.forEach((container) -> event.put(container.type, container.builder.build()));
    }

    private static class AttributeContainer {
        EntityType<? extends LivingEntity> type;
        AttributeSupplier.Builder builder;
        private AttributeContainer(EntityType<? extends LivingEntity> type, AttributeSupplier.Builder builder){
            this.type = type;
            this.builder = builder;
        }
    }
}
