/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.keybind.forge;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, value= Dist.CLIENT)
public class KeybindWrapperImpl {
    private static List<KeyMapping> KEYS = new ArrayList<>();

    public static void register(KeyMapping key){
        KEYS.add(key);
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event){
        KEYS.forEach(event::register);
    }
}
