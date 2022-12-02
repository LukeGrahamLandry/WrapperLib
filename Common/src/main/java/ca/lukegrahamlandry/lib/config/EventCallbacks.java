/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.config;

import ca.lukegrahamlandry.lib.base.event.IEventCallbacks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class EventCallbacks implements IEventCallbacks {
    @Override
    public void onServerStart(MinecraftServer server){
        ConfigWrapper.server = server;
        ConfigWrapper.ALL.forEach((config) -> {
            if (config.side.inWorldDir){
                config.load();
                config.sync();
            }
        });
    }

    @Override
    public void onPlayerLoginServer(Player player){
        if (player.level.isClientSide()) return;

        ConfigWrapper.ALL.forEach((config) -> {
            if (config.side == ConfigWrapper.Side.SYNCED){
                // TODO: dont have to resync to all players, just the new one
                config.sync();
            }
        });
    }

    @Override
    public void onClientSetup(){
        ConfigWrapper.ALL.forEach((config) -> {
            if (config.side == ConfigWrapper.Side.CLIENT){
                config.load();
            }
        });
    }

    // figure out dealing with client ones where the player might not have perms to use reload command
//    @Override
//    public void onReloadCommand(){
//        ConfigWrapper.ALL.forEach((config) -> {
//            if (config.reloadable){
//                config.load();
//                config.sync();
//            }
//        });
//    }
}
