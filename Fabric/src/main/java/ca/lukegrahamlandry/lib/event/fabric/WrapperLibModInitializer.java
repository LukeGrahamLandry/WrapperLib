/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.event.fabric;

import ca.lukegrahamlandry.lib.base.event.EventWrapper;
import ca.lukegrahamlandry.lib.base.event.IEventCallbacks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class WrapperLibModInitializer implements ModInitializer {
    /**
     * If you shadow WrapperLib you must manually call this method.
     */
    @Override
    public void onInitialize() {
        EventWrapper.get().forEach(IEventCallbacks::onInit);

        ServerLifecycleEvents.SERVER_STARTING.register(server -> EventWrapper.get().forEach((event) -> event.onServerStarting(server)));
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> EventWrapper.get().forEach((event) -> event.onServerStopped(server)));
        ServerWorldEvents.UNLOAD.register((server, world) -> EventWrapper.get().forEach((event) -> event.onLevelSave(world)));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> EventWrapper.get().forEach((event) -> event.onPlayerLoginServer(handler.getPlayer())));
    }
}
