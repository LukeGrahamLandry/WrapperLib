package ca.lukegrahamlandry.lib.entity;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

public class AttributeHelper {
    @ExpectPlatform
    public static void register(EntityType<? extends LivingEntity> type, AttributeSupplier.Builder builder) {
        throw new AssertionError();
    }
}
