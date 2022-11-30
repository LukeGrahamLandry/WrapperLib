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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;

import java.lang.reflect.Type;

public class NbtTypeAdapter implements JsonDeserializer<CompoundTag>, JsonSerializer<CompoundTag> {
    public NbtTypeAdapter() {
    }

    public CompoundTag deserialize(JsonElement data, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        try {
            return TagParser.parseTag(data.getAsString());
        } catch (CommandSyntaxException e) {
            throw new JsonParseException(e);
        }
    }

    public JsonElement serialize(CompoundTag obj, Type type, JsonSerializationContext ctx) {
        return new JsonPrimitive(obj.getAsString());
    }
}