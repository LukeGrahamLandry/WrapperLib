/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.examplemod;

import ca.lukegrahamlandry.examplemod.model.KillTracker;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ExampleEventHandlers {
    public static void onJump(LivingEntity entity){
        if (entity.level.isClientSide()) return;
        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, ExampleCommonMain.config.get().speedLevel));
    }

    public static void onJoin(Player player){
        if (player.level.isClientSide()) return;
        player.addItem(ExampleCommonMain.config.get().sword);
        System.out.println("This is the configured list.");
        System.out.println(ExampleCommonMain.list_test.get());
        System.out.println(ExampleCommonMain.list_test.get().getClass());
        System.out.println(ExampleCommonMain.list_test.get().get(0).getClass());
        ExampleCommonMain.list_test.get().forEach((entry) -> System.out.println(entry));
    }

    public static void onDeath(LivingEntity entity, DamageSource source){
        if (entity.level.isClientSide()) return;
        Entity killer = source.getEntity();
        if (killer instanceof Player){
            Player player = (Player) killer;
            ItemStack weapon = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (entity instanceof Player) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * ExampleCommonMain.weaponKills.get(weapon).players));
                ExampleCommonMain.kills.get(player).players++;
                ExampleCommonMain.weaponKills.get(weapon).players++;
            } else {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * ExampleCommonMain.weaponKills.get(weapon).mobs));
                ExampleCommonMain.kills.get(player).mobs++;
                ExampleCommonMain.weaponKills.get(weapon).mobs++;

            }
            ExampleCommonMain.kills.setDirty(player);
            ExampleCommonMain.weaponKills.setDirty(weapon);


        }
    }

    public static void drawOverlay(PoseStack stack) {
        KillTracker kills = ExampleCommonMain.kills.get(Minecraft.getInstance().player);
        Minecraft.getInstance().font.draw(stack, "Player Kills: " + kills.players, 20, 20, ExampleCommonMain.clientConfig.get().uiColour);
        Minecraft.getInstance().font.draw(stack, "Mob Kills: " + kills.mobs, 20, 40, ExampleCommonMain.clientConfig.get().uiColour);
    }
}
