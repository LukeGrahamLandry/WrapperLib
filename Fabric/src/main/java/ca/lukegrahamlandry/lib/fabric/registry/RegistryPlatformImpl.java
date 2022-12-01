/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.fabric.registry;

import ca.lukegrahamlandry.lib.registry.RegistryWrapper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Supplier;

public class RegistryPlatformImpl {
    public static <T> void register(Registry<T> registry, ResourceLocation rl, Supplier<T> constructor) {
        Registry.register(registry, rl, constructor.get());
    }

    /**
     * Fabric doesn't aggressively fuck with registries so this does nothing and vanilla handles everything
     */
    public static <T> void init(RegistryWrapper<T> wrapper) {

    }
}
