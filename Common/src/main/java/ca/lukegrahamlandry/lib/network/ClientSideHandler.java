/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.network;

import net.minecraft.server.level.ServerPlayer;

public interface ClientSideHandler {
    /**
     * The method to be called when an object of this type is received, through the network, on the client.
     * It is safe to call client only code here. Although the object will be initialized on the server, this method will never be called there so client classes referenced here will not be loaded.
     */
    void handle();

    default void sendToAllClients(){
        NetworkWrapper.sendToAllClients(this);
    }

    default void sendToClient(ServerPlayer player){
        NetworkWrapper.sendToClient(player, this);
    }
}
