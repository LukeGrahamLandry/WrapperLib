/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.helper;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

import java.util.function.Supplier;

public class EntityHelper {
    /**
     * Registers attributes for a custom living entity.
     * @param type the EntityType that will use these attributes.
     * @param builder the attributes to be registered.
     */
    @ExpectPlatform
    public static void attributes(Supplier<EntityType<? extends LivingEntity>> type, Supplier<AttributeSupplier.Builder> builder) {
        throw new AssertionError();
    }

    /**
     * This may ONLY be called on the CLIENT.
     * @param type the entity type that will be bound to
     * @param renderer the constructor of your EntityRenderer class
     */
    @ExpectPlatform
    public static <E extends Entity> void renderer(Supplier<EntityType<? extends E>> type, EntityRendererProvider<E> renderer) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static ModdedSpawnEggFactory getSpawnEggConstructor() {
        throw new AssertionError();
    }

    public interface ModdedSpawnEggFactory {
        SpawnEggItem create(Supplier<EntityType<? extends Mob>> type, int backgroundColor, int highlightColor, Item.Properties props);
    }

    public interface EntityRendererProvider<E extends Entity> {
        EntityRenderer<E> create(EntityRenderDispatcher manager);
    }
}
