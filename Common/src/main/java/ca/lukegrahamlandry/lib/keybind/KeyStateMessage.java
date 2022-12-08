/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.keybind;

import ca.lukegrahamlandry.lib.network.ServerSideHandler;
import net.minecraft.server.level.ServerPlayer;

public class KeyStateMessage implements ServerSideHandler {
    String id;
    boolean pressed;
    public KeyStateMessage(KeybindWrapper key){
        this.id = key.mapping.getName();
        this.pressed = key.mapping.isDown();
    }

    @Override
    public void handle(ServerPlayer player) {
        KeybindWrapper wrapper = KeybindWrapper.ALL.get(id);
        if (wrapper == null){
            return;
        }

        wrapper.pressed.put(player.getUUID(), pressed);

        if (pressed) {
            wrapper.onPressAction.accept(player);
        } else {
            wrapper.onReleaseAction.accept(player);
        }
    }
}
