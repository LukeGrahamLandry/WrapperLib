/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.registry.forge;

import ca.lukegrahamlandry.lib.registry.RegistryWrapper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class RegistryWrapperImpl {
    public static <T> void register(Registry<T> registry, ResourceLocation rl, Supplier<T> constructor) {
        RegistryContainer.of(registry, rl.getNamespace()).deferred.register(rl.getPath(), constructor);
    }

    public static <T> void init(RegistryWrapper<T> wrapper) {
        RegistryContainer.of(wrapper.registry, wrapper.modid).init();
    }

    private static class RegistryContainer<T> {
        final DeferredRegister<T> deferred;
        final String modid;
        private RegistryContainer(Registry<T> registry, String modid){
            this.deferred = DeferredRegister.create(RegistryManager.ACTIVE.getRegistry(registry.key()), modid);
            this.modid = modid;
        }

        public void init(){
            this.deferred.register(this.getModEventBus());
        }

        private static final Map<String, RegistryContainer<?>> registries = new HashMap<>();
        private static <T> RegistryContainer<T> of(Registry<T> registry, String modid){
            String descriptor = modid + "-" + registry.key().location();
            if (!registries.containsKey(descriptor)) {
                registries.put(descriptor, new RegistryContainer<T>(registry, modid));
            }
            return (RegistryContainer<T>) registries.get(descriptor);
        }

        /**
         * Each mod has a personal event bus and if you register objects on a bus that doesn't match their namespace, it warns you.
         * @return the correct mod event bus for our mod id. defaults to WrapperLib's event bus if a matching mod could not be found.
         */
        private IEventBus getModEventBus(){
            IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
            Optional<? extends ModContainer> parentMod = ModList.get().getModContainerById(this.modid);
            if (parentMod.isPresent() && parentMod.get() instanceof FMLModContainer) bus = ((FMLModContainer) parentMod.get()).getEventBus();
            return bus;
        }
    }
}
