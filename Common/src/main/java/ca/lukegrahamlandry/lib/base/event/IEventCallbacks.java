/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.base.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;

public interface IEventCallbacks {
    default void onServerStart(MinecraftServer server) {}

    default void onServerStop(MinecraftServer server) {}

    default void onLevelSave(LevelAccessor level) {}

    default void onPlayerLogin(Player player) {}

    default void onClientSetup() {}

    // currently must be called in mod constructor if shadowing and excluding mod class because no event annotation for this
    default void onInit() {}

    default void onReloadCommand() {}
}
