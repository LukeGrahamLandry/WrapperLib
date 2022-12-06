/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.helper.forge;

import ca.lukegrahamlandry.lib.helper.EntityHelper;
import ca.lukegrahamlandry.lib.helper.forge.entity.AddEntityAttributesImpl;
import ca.lukegrahamlandry.lib.helper.forge.entity.AddEntityRendererImpl;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.common.ForgeSpawnEggItem;

import java.util.function.Supplier;

public class EntityHelperImpl {
    public static void attributes(Supplier<EntityType<? extends LivingEntity>> type, Supplier<AttributeSupplier.Builder> builder) {
        AddEntityAttributesImpl.add(type, builder);
    }

    public static <E extends Entity> void renderer(Supplier<EntityType<? extends E>> type, EntityRendererProvider<E> renderer) {
        AddEntityRendererImpl.add(type, renderer);
    }

    public static EntityHelper.ModdedSpawnEggFactory getSpawnEggConstructor() {
        return ForgeSpawnEggItem::new;
    }
}
