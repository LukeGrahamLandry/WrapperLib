/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.data.impl;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class LevelDataWrapper<T> extends MapDataWrapper<Level, ResourceLocation, T> {
    public LevelDataWrapper(Class<T> clazz) {
        super(ResourceLocation.class, clazz);
    }

    @Override
    public ResourceLocation keyToId(Level key) {
        return key.dimension().location();
    }

    @Override
    public ResourceLocation stringToId(String id) {
        return new ResourceLocation(id);
    }
}
