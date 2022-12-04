/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.examplemod.model;

import ca.lukegrahamlandry.lib.network.ClientSideHandler;
import net.minecraft.client.Minecraft;

public class ExamplePacket implements ClientSideHandler {
    private final int y;

    public ExamplePacket(int y){
        this.y = y;
    }

    @Override
    public void handle() {
        Minecraft.getInstance().player.setDeltaMovement(0, this.y, 0);
    }
}
