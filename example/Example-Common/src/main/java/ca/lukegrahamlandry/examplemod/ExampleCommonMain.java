/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.examplemod;

import ca.lukegrahamlandry.examplemod.model.HudColorConfig;
import ca.lukegrahamlandry.examplemod.model.ExampleConfig;
import ca.lukegrahamlandry.examplemod.model.KillTracker;
import ca.lukegrahamlandry.examplemod.obj.ListItem;
import ca.lukegrahamlandry.lib.config.ConfigWrapper;
import ca.lukegrahamlandry.lib.data.DataWrapper;
import ca.lukegrahamlandry.lib.data.impl.PlayerDataWrapper;
import ca.lukegrahamlandry.lib.keybind.KeybindWrapper;
import ca.lukegrahamlandry.lib.network.NetworkWrapper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.Supplier;

public class ExampleCommonMain {
    public static final String MOD_ID = "wrapperlibexamplemod";

    public static final Supplier<ExampleConfig> config = ConfigWrapper.synced(ExampleConfig.class).named(MOD_ID);
    public static final Supplier<HudColorConfig> clientConfig = ConfigWrapper.client(HudColorConfig.class);
    public static final ConfigWrapper<List<ListItem>> list_test = ConfigWrapper.server(ListItem.class).listOf().named(MOD_ID + "list");

    public static final PlayerDataWrapper<KillTracker> kills = DataWrapper.player(KillTracker.class).synced().saved().dir(MOD_ID).named("kills");

    public static void init(){
        NetworkWrapper.handshake(MOD_ID, "1");

        // ensure RegistryTest is class loaded
        RegistryTest.ITEMS.init();

        KeybindWrapper.of("float", MOD_ID, GLFW.GLFW_KEY_Z).synced()
                .onHeldTick(player -> {
                    if (!player.level.isClientSide()) {
                        player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 20));
                    }
                }).onRelease(player -> {
                    if (!player.level.isClientSide()) {
                        player.removeEffect(MobEffects.LEVITATION);
                    }
                });
    }
}
