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
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.SpawnEggItem;

import java.util.function.Supplier;

public class EntityHelperImpl {
    public static void attributes(Supplier<EntityType<? extends LivingEntity>> type, Supplier<AttributeSupplier.Builder> builder) {
        FabricDefaultAttributeRegistry.register(type.get(), builder.get());
    }

    public static <E extends Entity> void renderer(Supplier<EntityType<? extends E>> type, EntityHelper.EntityRendererProvider<E> renderer) {
        EntityRendererRegistry.INSTANCE.register(type.get(), (manager, ctx) -> renderer.create(manager));
    }

    public static EntityHelper.ModdedSpawnEggFactory getSpawnEggConstructor() {
        // fabric doesn't mess with registries, so it's safe to invoke the supplier immediately
        return (type, backgroundColor, highlightColor, props) -> new SpawnEggItem(type.get(), backgroundColor, highlightColor, props);
    }
}
