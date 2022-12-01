/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Supplier;


public class RegistryWrapper<T> {
    public static <T> RegistryWrapper<T> of(Registry<T> vanillaRegistry, String modid){
        return new RegistryWrapper<>(vanillaRegistry, modid);
    }

    public final Registry<T> registry;
    public final String modid;
    public RegistryWrapper(Registry<T> vanillaRegistry, String modid) {
        this.registry = vanillaRegistry;
        this.modid = modid;
    }

    /**
     * Enqueue an object to be registered.
     * @param name The location to register the object.
     * @param constructor A supplier for your object. You cannot use a direct item instance because Forge is weird.
     * @return A supplier for your object that will only resolve after registration has been handled.
     */
    public Supplier<T> register(String name, Supplier<T> constructor){
        ResourceLocation rl = new ResourceLocation(this.modid, name);
        RegistryPlatform.register(this.registry, rl, constructor);
        return () -> this.registry.get(rl);
    }

    public void init(){
        RegistryPlatform.init(this);
    }
}
