/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.event.fabric;

import ca.lukegrahamlandry.lib.base.Available;
import ca.lukegrahamlandry.lib.base.event.EventWrapper;
import ca.lukegrahamlandry.lib.base.event.IEventCallbacks;
import ca.lukegrahamlandry.lib.keybind.KeybindTickCallbacks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class WrapperLibModInitializer implements ModInitializer {
    /**
     * If you shadow WrapperLib you must manually call this method.
     */
    @Override
    public void onInitialize() {
        EventWrapper.init();

        ServerLifecycleEvents.SERVER_STARTING.register(server -> EventWrapper.get().forEach((event) -> event.onServerStarting(server)));
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> EventWrapper.get().forEach((event) -> event.onServerStopped(server)));
        ServerWorldEvents.UNLOAD.register((server, world) -> EventWrapper.get().forEach((event) -> event.onLevelSave(world)));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> EventWrapper.get().forEach((event) -> event.onPlayerLoginServer(handler.getPlayer())));

        if (Available.KEYBIND.get())
            ServerTickEvents.END_WORLD_TICK.register(
                    world -> world.players().forEach(
                            p -> KeybindTickCallbacks.onServerPlayerTick(p)));  // if you use method reference it tries to class load LocalPlayer, idk man

        if (Available.CONFIG.get())
            ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((server, resourceManager) -> EventWrapper.get().forEach(IEventCallbacks::onReloadCommand));
    }
}
