package ca.lukegrahamlandry.lib.helper.forge.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class AddEntityAttributesImpl {
    public static final List<AttributeContainer> attributes = new ArrayList<>();

    public static void add(Supplier<EntityType<? extends LivingEntity>> type, Supplier<AttributeSupplier.Builder> builder) {
        attributes.add(new AttributeContainer(type, builder));
    }

    @SubscribeEvent
    public static void handleAttributeEvent(EntityAttributeCreationEvent event){
        attributes.forEach((container) -> event.put(container.type.get(), container.builder.get().build()));
    }

    private static class AttributeContainer {
        final Supplier<EntityType<? extends LivingEntity>> type;
        final Supplier<AttributeSupplier.Builder> builder;
        private AttributeContainer(Supplier<EntityType<? extends LivingEntity>> type, Supplier<AttributeSupplier.Builder> builder){
            this.type = type;
            this.builder = builder;
        }
    }
}
