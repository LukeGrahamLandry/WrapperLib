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

public interface ServerSideHandler {
    /**
     * The method to be called when an object of this type is received, through the network, on the server.
     * @param player the player whose client sent the packet
     */
    void handle(ServerPlayer player);

    // SENDING HELPER

    default void sendToServer(){
        NetworkWrapper.sendToServer(this);
    }
}
