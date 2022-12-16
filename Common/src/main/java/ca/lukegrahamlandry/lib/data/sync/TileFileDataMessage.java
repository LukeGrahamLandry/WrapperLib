/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.data.sync;

import ca.lukegrahamlandry.lib.base.WorkInProgress;
import ca.lukegrahamlandry.lib.data.DataWrapper;
import ca.lukegrahamlandry.lib.data.impl.MapDataWrapper;
import ca.lukegrahamlandry.lib.data.impl.TileFileDataWrapper;
import ca.lukegrahamlandry.lib.network.ClientSideHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

@WorkInProgress
public class TileFileDataMessage implements ClientSideHandler {
    String value;
    BlockPos pos;
    ResourceLocation dimension;
    String name;
    String dir;

    public <T> TileFileDataMessage(TileFileDataWrapper<T> wrapper, BlockEntity tile) {
        this.name = wrapper.getName();
        this.dir = wrapper.getSubDirectory();
        this.pos = tile.getBlockPos();
        this.dimension = tile.getLevel().dimension().location();

        // encode here using ConfigWrapper#getGson instead of allowing the object to be encoded by the packet module's gson instance
        // this allows adding type adapters to your ConfigWrapper and still having syncing
        // TODO: since not using a GenericHolder, data may not be a subclass of V
        this.value = wrapper.getGson().toJson(wrapper.get(tile));
    }

    public void handle() {
        boolean handled = false;
        for (DataWrapper<?, ?> data : DataWrapper.ALL) {
            if (data instanceof TileFileDataWrapper<?> && Objects.equals(this.dir, data.getSubDirectory()) && data.getName().equals(this.name)) {
                Object syncedValue = data.getGson().fromJson(this.value, data.getValueClass());
                ((TileFileDataWrapper<?>) data).set(dimension, pos, syncedValue);
            }
        }

        if (!handled) DataWrapper.LOGGER.error("SingleMap. Received data sync for unknown {name: " + this.name + ", dir: " + this.dir + "}");
    }
}
