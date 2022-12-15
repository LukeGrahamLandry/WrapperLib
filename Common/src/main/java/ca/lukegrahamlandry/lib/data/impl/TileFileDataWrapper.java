/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.data.impl;

import ca.lukegrahamlandry.lib.base.WorkInProgress;
import ca.lukegrahamlandry.lib.data.sync.TileFileDataMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Map;

@WorkInProgress
public class TileFileDataWrapper<V> extends LevelDataWrapper<Map<BlockPos, V>> {
    public TileFileDataWrapper(Class<Map<BlockPos, V>> clazz) {
        super(clazz);  // wont work until i switch to using type tokens
    }

    public V get(BlockEntity key) {
        if (!key.hasLevel()) return null;
        return get(key.getLevel()).get(key.getBlockPos());
    }

    public void setDirty(BlockEntity key) {
        this.isDirty = true;
        if (!key.hasLevel()) return;
        if (this.shouldSync) new TileFileDataMessage(this, key).sendToAllClients();
    }

    public void clear(BlockEntity key) {
        if (!key.hasLevel()) return;
        get(key.getLevel()).remove(key.getBlockPos());
    }

    public void set(ResourceLocation dim, BlockPos pos, Object value) {
        getById(dim).put(pos, (V) value);
    }
}
