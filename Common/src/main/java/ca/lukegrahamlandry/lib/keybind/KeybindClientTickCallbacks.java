/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.keybind;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class KeybindClientTickCallbacks {
    public static void onClientTick(){
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        for (KeybindWrapper key : KeybindWrapper.ALL.values()){
            boolean wasDown = key.isPressed(player);
            if (key.mapping.isDown()){
                if (!wasDown){
                    key.pressed.put(player.getUUID(), true);
                    key.onPressAction.accept(player);
                    if (key.shouldSync) new KeyStateMessage(key).sendToServer();
                }
                key.onHeldTickAction.accept(player);
            } else if (wasDown) {
                key.pressed.put(player.getUUID(), false);
                key.onReleaseAction.accept(player);
                if (key.shouldSync) new KeyStateMessage(key).sendToServer();
            }
        }
    }
}
