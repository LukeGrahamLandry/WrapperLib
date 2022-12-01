package ca.lukegrahamlandry.lib.entity;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

public class EntityHelper {
    /**
     * Registers attributes for a custom living entity.
     * @param type the EntityType that will use these attributes.
     * @param builder the attributes to be registered.
     */
    @ExpectPlatform
    public static void attributes(EntityType<? extends LivingEntity> type, AttributeSupplier.Builder builder) {
        throw new AssertionError();
    }
}
