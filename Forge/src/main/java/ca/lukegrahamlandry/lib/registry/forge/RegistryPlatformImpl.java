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
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryManager;

import java.util.Map;
import java.util.function.Supplier;

public class RegistryPlatformImpl {
    public static <T> void init(RegistryWrapper<T> wrapper){
        DeferredRegister<T> register = DeferredRegister.create(RegistryManager.ACTIVE.getRegistry(wrapper.registry.key()), wrapper.modid);
        for (Map.Entry<String, Supplier<T>> entry : wrapper.entrySet()){
            register.register(entry.getKey(), entry.getValue());
        }
        register.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
