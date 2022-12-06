/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.function.Supplier;

/**
 * A platform independent wrapper around Minecraft's registry system.
 * @param <T> the type of object that will be registered
 */
public class RegistryWrapper<T> {
    /**
     * @param vanillaRegistry where objects will be registered. Registry.OBJECT_TYPE
     * @param modid will be used as the path of your object's registry name.
     * @return a RegistryWrapper that allows you to register new game objects.
     */
    public static <T> RegistryWrapper<T> create(Registry<T> vanillaRegistry, String modid){
        return new RegistryWrapper<>(vanillaRegistry, modid);
    }

    /**
     * Enqueue an object to be registered.
     * @param name The location to register the object.
     * @param constructor A supplier for your object. You cannot use a direct instance because Forge is weird.
     * @return A supplier for your object that will only resolve after registration has been handled (which is immediately on fabric).
     */
    public <O extends T> RegistryThing<O> register(String name, Supplier<O> constructor){
        ResourceLocation rl = new ResourceLocation(this.modid, name);
        register(this.registry, rl, constructor);
        return new RegistryThing<>(this.registry, rl);
    }

    /**
     * Calling this is optional.
     * This method does nothing but serves as a reminder and semantically pleasing way to class load your registry class.
     * For example, if you static init this in your ItemInit class, you must ensure it gets class loaded during your mod initialization so your items are registered in time. So you may choose to call this method from your mod initializer.
     */
    public void init(){}

    // HELPER

    /**
     * Register a new entity type without manually calling EntityType.Builder#build
     */
    public <E extends Entity> RegistryThing<EntityType<E>> register(String name, EntityType.Builder<E> entityBuilder){
        ResourceLocation rl = new ResourceLocation(this.modid, name);
        register(this.registry, rl, () -> (T) entityBuilder.build(name));
        return new RegistryThing<>(this.registry, rl);
    }

    // IMPL

    public final Registry<T> registry;
    public final String modid;
    private RegistryWrapper(Registry<T> vanillaRegistry, String modid) {
        this.registry = vanillaRegistry;
        this.modid = modid;
    }

    /**
     * Preform platform specific registration of a single object.
     */
    @ExpectPlatform
    public static <T> void register(Registry<T> registry, ResourceLocation rl, Supplier<? extends T> constructor) {
        throw new AssertionError();
    }
}
