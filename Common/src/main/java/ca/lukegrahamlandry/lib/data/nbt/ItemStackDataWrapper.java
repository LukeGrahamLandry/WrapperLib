/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.data.nbt;

import com.google.gson.reflect.TypeToken;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.Random;

// TODO: do i need to sync manually?
public class ItemStackDataWrapper<V> extends NbtDataWrapper<ItemStack, V, ItemStackDataWrapper<V>> {
    public static final String ID_TAG_KEY = "_id";
    private static final Random rand = new Random();

    public ItemStackDataWrapper(TypeToken<V> type) {
        super(type);
    }

    protected CompoundTag getSharedTag(ItemStack obj){
        if (!obj.hasTag()) obj.setTag(obj.getOrCreateTag());
        CompoundTag tag = obj.getTag();
        if (!tag.contains(PARENT_TAG_KEY)) tag.put(PARENT_TAG_KEY, new CompoundTag());
        return tag.getCompound(PARENT_TAG_KEY);
    }

    @Override
    protected int getHashCode(ItemStack obj) {
        CompoundTag tag = getSharedTag(obj);
        if (!tag.contains(ID_TAG_KEY)) tag.putInt(ID_TAG_KEY, rand.nextInt());
        return tag.getInt(ID_TAG_KEY);
    }
}
