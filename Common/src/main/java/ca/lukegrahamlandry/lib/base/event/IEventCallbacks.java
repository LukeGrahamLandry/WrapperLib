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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.NotNull;

@InternalUseOnly
public interface IEventCallbacks {
    default void onServerStarting(@NotNull MinecraftServer server) {}

    default void onServerStopped(@NotNull MinecraftServer server) {}

    default void onLevelSave(@NotNull LevelAccessor level) {}

    default void onPlayerLoginServer(@NotNull Player player) {}

    default void onClientSetup() {}

    default void onInit() {}

    default void onReloadCommand() {}
}
