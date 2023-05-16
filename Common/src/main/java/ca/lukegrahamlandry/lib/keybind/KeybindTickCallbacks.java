/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.keybind;

import net.minecraft.server.level.ServerPlayer;

public class KeybindTickCallbacks {
    public static void onServerPlayerTick(ServerPlayer player){
        for (KeybindWrapper key : KeybindWrapper.ALL.values()){
            if (key.isPressed(player)){
                key.onHeldTickAction.accept(player);
            }
        }
    }
}
