/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.event.forge;

import ca.lukegrahamlandry.lib.base.event.EventWrapper;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;

@Mod.EventBusSubscriber(bus= Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventListeners {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onServerStarting(FMLServerStartingEvent event){
        EventWrapper.get().forEach((handler) -> handler.onServerStarting(event.getServer()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onServerStopped(FMLServerStoppedEvent event){
        EventWrapper.get().forEach((handler) -> handler.onServerStopped(event.getServer()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLevelSave(WorldEvent.Save event){
        EventWrapper.get().forEach((handler) -> handler.onLevelSave(event.getWorld()));
    }

    // server side only it seems
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event){
        EventWrapper.get().forEach((handler) -> handler.onPlayerLoginServer(event.getPlayer()));
    }
}
