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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.function.Supplier;

public class EntityHelper {
    /**
     * Registers attributes for a custom living entity.
     * @param type the EntityType that will use these attributes.
     * @param builder the attributes to be registered.
     */
    @ExpectPlatform
    public static void attributes(Supplier<EntityType<? extends LivingEntity>> type, AttributeSupplier.Builder builder) {
        throw new AssertionError();
    }
}
