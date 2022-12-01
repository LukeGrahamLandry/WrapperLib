/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.registry.forge;

import ca.lukegrahamlandry.lib.base.event.IEventCallbacks;
import ca.lukegrahamlandry.lib.registry.RegistryWrapper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryManager;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class RegistryPlatformImpl implements IEventCallbacks {
    public static Map<String, RegistryContainer<?>> registries = new HashMap<>();

    public static <T> void register(Registry<T> registry, ResourceLocation rl, Supplier<T> constructor) {
        RegistryContainer.of(registry, rl.getNamespace()).deferred.register(rl.getPath(), constructor);
    }

    /**
     * Ensures the specified RegistryWrapper's RegistryContainer has been subscribed to the mod event bus.
     * In it will be done automatically by RegistryPlatformImpl#onInit but if IEventCallbacks#init fired before your mod was constructed, your RegistryWrapper may not have been added to the list in time.
     */
    public static <T> void init(RegistryWrapper<T> wrapper) {
        RegistryContainer.of(wrapper.registry, wrapper.modid).init();
    }

    /**
     * Ensures any RegistryContainers that exist so far have been subscribed to the mod event bus.
     */
    @Override
    public void onInit(){
        RegistryPlatformImpl.registries.values().forEach(RegistryPlatformImpl.RegistryContainer::init);
    }

    public static class RegistryContainer<T> {
        DeferredRegister<T> deferred;
        boolean isInitialized;
        private RegistryContainer(Registry<T> registry, String modid){
            this.deferred = DeferredRegister.create(RegistryManager.ACTIVE.getRegistry(registry.key()), modid);
            this.isInitialized = false;
        }

        public void init(){
            if (!this.isInitialized){
                this.deferred.register(FMLJavaModLoadingContext.get().getModEventBus());
                this.isInitialized = true;
            }
        }

        public static <T> RegistryContainer<T> of(Registry<T> registry, String modid){
            String descriptor = modid + "-" + registry.key().location();
            if (!registries.containsKey(descriptor)) {
                registries.put(descriptor, new RegistryContainer<T>(registry, modid));
            }
            return (RegistryContainer<T>) registries.get(descriptor);
        }
    }
}
