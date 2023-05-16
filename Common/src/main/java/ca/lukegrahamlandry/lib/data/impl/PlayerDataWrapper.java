/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.data.impl;

import com.google.gson.reflect.TypeToken;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

// TODO: something clever for only syncing tracked players
/**
 * Data is saved per uuid not per player entity so data is maintained between deaths and dimension changes.
 */
public class PlayerDataWrapper<T> extends MapDataWrapper<Player, UUID, T, PlayerDataWrapper<T>> {
    public PlayerDataWrapper(TypeToken<T> type) {
        super(UUID.class, type);
    }

    @Override
    public @NotNull UUID keyToId(@NotNull Player key) {
        return key.getUUID();
    }

    @Override
    public @NotNull UUID stringToId(@NotNull String id) {
        return UUID.fromString(id);
    }
}
