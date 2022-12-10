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
import net.minecraft.world.item.ItemStack;

@WorkInProgress
public class ItemStackDataWrapper<V> extends NbtDataWrapper<ItemStack, V> {
    protected CompoundTag getTag(ItemStack obj){
        if (!obj.hasTag()) obj.setTag(obj.getOrCreateTag());
        return obj.getTag();
    }

    @Override
    protected int getHashCode(ItemStack obj) {

        return 0;
    }
}
