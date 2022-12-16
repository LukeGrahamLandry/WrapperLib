/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.resources.forge;

import ca.lukegrahamlandry.lib.resources.ResourcesWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;


@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE, value=Dist.CLIENT)
public class ClientListenersEventListener {
    public static final List<ResourcesWrapper<?>> CLIENT_LISTENERS = new ArrayList<>();

    @SubscribeEvent
    public static void registerClientListener(ModelRegistryEvent event){
        CLIENT_LISTENERS.forEach((listener) -> ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(listener));
    }
}
