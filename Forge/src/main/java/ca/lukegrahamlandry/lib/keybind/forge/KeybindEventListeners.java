/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.keybind.forge;

import ca.lukegrahamlandry.lib.keybind.KeybindTickCallbacks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(bus= Mod.EventBusSubscriber.Bus.FORGE)
public class KeybindEventListeners {
    @SubscribeEvent
    public static void onServerTick(TickEvent.PlayerTickEvent event){
        if (!event.player.level().isClientSide() && event.phase == TickEvent.Phase.END) KeybindTickCallbacks.onServerPlayerTick((ServerPlayer) event.player);
    }
}
