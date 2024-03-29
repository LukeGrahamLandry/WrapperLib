/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.resources;

import ca.lukegrahamlandry.lib.base.event.IEventCallbacks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class EventCallbacks implements IEventCallbacks {
    @Override
    public void onPlayerLoginServer(Player player){
        if (player.level.isClientSide()) return;

        ResourcesWrapper.ALL.forEach((resources) -> {
            if (resources.shouldSync) new DataPackSyncMessage(resources).sendToClient((ServerPlayer) player);
        });
    }

    @Override
    public void onServerStarting(MinecraftServer server){
        ResourcesWrapper.server = server;
    }

    @Override
    public void onServerStopped(MinecraftServer server){
        ResourcesWrapper.server = null;
    }
}
