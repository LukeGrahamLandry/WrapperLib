/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.base.json;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;

import java.lang.reflect.Type;

public class BlockPosTypeAdapter implements JsonDeserializer<BlockPos>, JsonSerializer<BlockPos> {
    public BlockPosTypeAdapter() {
    }

    public BlockPos deserialize(JsonElement data, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        String values = data.getAsString();
        String[] parts = values.split(",");
        return new BlockPos(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
    }

    public JsonElement serialize(BlockPos obj, Type type, JsonSerializationContext ctx) {
        String values = obj.getX() + "," + obj.getY() + "," + obj.getZ();
        return new JsonPrimitive(values);
    }
}