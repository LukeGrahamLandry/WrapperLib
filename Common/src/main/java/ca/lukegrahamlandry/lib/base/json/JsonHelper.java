/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.base.json;

import ca.lukegrahamlandry.lib.base.GenericHolder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Type;

public class JsonHelper {
    private static final GsonBuilder GSON_BUILDER = new GsonBuilder().setLenient()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .registerTypeAdapter(GenericHolder.class, new GenericHolder.TypeAdapter())
            .registerTypeAdapter(CompoundTag.class, new NbtTypeAdapter())
            .registerTypeAdapter(ItemStack.class, new ItemStackTypeAdapter());

    private static Gson GSON = GSON_BUILDER.create();

    public static Gson get(){
        return GSON;
    }

    /**
     * When not shadowing, please prefer using ConfigWrapper#withGson or DataWrapper#withGson because that will ensure you do not conflict with other mods.
     * @param type the type of object that needs custom serialization handling
     * @param typeAdapter the type adapter that will handle serialization (instanceof TypeAdapter, JsonSerializer, JsonDeserializer, or InstanceCreator)
     */
    public static void addTypeAdapter(Type type, Object typeAdapter){
        GSON_BUILDER.registerTypeAdapter(type, typeAdapter);
        init();
    }

    /**
     * When not shadowing, please prefer using ConfigWrapper#withGson or DataWrapper#withGson because that will ensure you do not conflict with other mods.
     */
    public static void addTypeAdapterFactory(TypeAdapterFactory factory){
        GSON_BUILDER.registerTypeAdapterFactory(factory);
        init();
    }

    private static void init(){
        GSON = GSON_BUILDER.create();
    }
}
