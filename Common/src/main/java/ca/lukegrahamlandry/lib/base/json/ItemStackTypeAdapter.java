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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Type;

public class ItemStackTypeAdapter implements JsonDeserializer<ItemStack>, JsonSerializer<ItemStack> {
    public ItemStackTypeAdapter() {
    }

    public ItemStack deserialize(JsonElement data, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        JsonObject json = data.getAsJsonObject();
        ResourceLocation itemKey = new ResourceLocation(json.get("item").getAsString());
        Item item = BuiltInRegistries.ITEM.get(itemKey);
        int count = json.get("count").getAsInt();

        ItemStack stack = new ItemStack(item, count);

        if (json.has("tag")){
            CompoundTag tag = ctx.deserialize(json.get("tag"), CompoundTag.class);
            stack.setTag(tag);
        }

        return stack;
    }

    public JsonElement serialize(ItemStack obj, Type type, JsonSerializationContext ctx) {
        JsonObject out = new JsonObject();
        out.addProperty("item", BuiltInRegistries.ITEM.getKey(obj.getItem()).toString());
        out.addProperty("count", obj.getCount());
        if (obj.hasTag()) out.add("tag", ctx.serialize(obj.getTag()));
        return out;
    }
}
