/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.helper.fabric;

import ca.lukegrahamlandry.lib.helper.EntityHelper;
import ca.lukegrahamlandry.lib.helper.fabric.entity.ModdedSpawnEggItem;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.function.Supplier;

public class EntityHelperImpl {
    public static void attributes(Supplier<EntityType<? extends LivingEntity>> type, Supplier<AttributeSupplier.Builder> builder) {
        FabricDefaultAttributeRegistry.register(type.get(), builder.get());
    }

    public static <E extends Entity> void renderer(Supplier<EntityType<? extends E>> type, EntityRendererProvider<E> renderer) {
        EntityRendererRegistry.register(type.get(), renderer);
    }


    public static EntityHelper.ModdedSpawnEggFactory getSpawnEggConstructor() {
        return ModdedSpawnEggItem::new;
    }
}
