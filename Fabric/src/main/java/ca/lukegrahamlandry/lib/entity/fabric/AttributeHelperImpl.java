package ca.lukegrahamlandry.lib.entity.fabric;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

public class AttributeHelperImpl {
    public static void register(EntityType<? extends LivingEntity> type, AttributeSupplier.Builder builder) {
        FabricDefaultAttributeRegistry.register(type, builder);
    }
}
