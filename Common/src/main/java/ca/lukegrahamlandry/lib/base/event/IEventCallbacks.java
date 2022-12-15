/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.base.event;

import ca.lukegrahamlandry.lib.base.InternalUseOnly;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;

@InternalUseOnly
public interface IEventCallbacks {
    default void onServerStarting(MinecraftServer server) {}

    default void onServerStopped(MinecraftServer server) {}

    default void onLevelSave(LevelAccessor level) {}

    default void onPlayerLoginServer(Player player) {}

    default void onClientSetup() {}

    default void onInit() {}

    // TODO. needs mixin?
    // default void onReloadCommand() {}
}
