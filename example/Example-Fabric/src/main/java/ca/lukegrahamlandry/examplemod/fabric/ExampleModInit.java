/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.examplemod.fabric;

import ca.lukegrahamlandry.examplemod.ExampleCommonMain;
import ca.lukegrahamlandry.examplemod.ExampleEventHandlers;
import ca.lukegrahamlandry.lib.event.fabric.WrapperLibModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class ExampleModInit implements ModInitializer {
    @Override
    public void onInitialize() {
        new WrapperLibModInitializer().onInitialize();
        ExampleCommonMain.init();
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ExampleEventHandlers.onJoin(handler.player));
    }
}
