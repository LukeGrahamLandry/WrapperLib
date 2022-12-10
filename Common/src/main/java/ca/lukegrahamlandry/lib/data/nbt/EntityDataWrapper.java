/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.data.nbt;

import ca.lukegrahamlandry.lib.base.WorkInProgress;
import ca.lukegrahamlandry.lib.data.NbtDataWrapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

@WorkInProgress
public class EntityDataWrapper<V> extends NbtDataWrapper<Entity, V> {
    protected CompoundTag getTag(Entity obj){
        return null;
    }

    @Override
    protected int getHashCode(Entity obj) {
        return obj.getUUID().hashCode();
    }
}
