/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.examplemod.forge;

import ca.lukegrahamlandry.examplemod.ExampleEventHandlers;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ExampleEvents {
    @SubscribeEvent
    public static void onJump(LivingEvent.LivingJumpEvent event){
        ExampleEventHandlers.onJump(event.getEntity());
    }

    @SubscribeEvent
    public static void onJoin(PlayerEvent.PlayerLoggedInEvent event){
        ExampleEventHandlers.onJoin(event.getEntity());
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event){
        ExampleEventHandlers.onDeath(event.getEntity(), event.getSource());
    }
}
