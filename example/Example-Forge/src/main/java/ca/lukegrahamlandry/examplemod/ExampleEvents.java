/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.examplemod;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ExampleEvents {
    @SubscribeEvent
    public static void onJump(LivingEvent.LivingJumpEvent event){
        if (event.getEntity().level.isClientSide()) return;
        event.getEntity().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, ExampleModMain.config.get().speedLevel));
    }

    @SubscribeEvent
    public static void onJoin(PlayerEvent.PlayerLoggedInEvent event){
        if (event.getEntity().level.isClientSide()) return;
        event.getEntity().addItem(ExampleModMain.config.get().sword);
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event){
        if (event.getEntity().level.isClientSide()) return;
        Entity killer = event.getSource().getEntity();
        if (killer instanceof Player){
            Player player = (Player) killer;
            if (event.getEntity() instanceof Player) ExampleModMain.kills.get(player).players++;
            else ExampleModMain.kills.get(player).mobs++;
            ExampleModMain.kills.setDirty(player);
        }
    }
}
