/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.network;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface ClientSideHandler {
    /**
     * The method to be called when an object of this type is received, through the network, on the client.
     * It is safe to call client only code here. Although the object will be initialized on the server, this method will never be called there so client classes referenced here will not be loaded.
     */
    void handle();

    // SENDING HELPERS

    default void sendToClient(ServerPlayer player){
        NetworkWrapper.sendToClient(player, this);
    }

    default void sendToAllClients(){
        NetworkWrapper.sendToAllClients(this);
    }

    default void sendToTrackingClients(ServerLevel level){
        NetworkWrapper.sendToTrackingClients(level, this);
    }

    default void sendToTrackingClients(ServerLevel world, BlockPos pos){
        NetworkWrapper.sendToTrackingClients(world, pos, this);
    }

    default void sendToTrackingClients(BlockEntity tile){
        NetworkWrapper.sendToTrackingClients(tile, this);
    }

    default void sendToTrackingClients(Entity entity){
        NetworkWrapper.sendToTrackingClients(entity, this);
    }

    default void sendToTrackingClientsAndSelf(ServerPlayer player){
        NetworkWrapper.sendToTrackingClientsAndSelf(player, this);
    }
}
