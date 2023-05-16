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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Type;
import java.util.Locale;

public class JsonHelper {
    private static final GsonBuilder GSON_BUILDER = new GsonBuilder().setLenient();
    private static Gson GSON;
    public static Gson get(){
        return GSON;
    }

    // stored separately instead of just using Gson#newBuilder to change it as needed because the version of gson shipped
    // with 1.16.5 (2.8.0) does not have that method. doing it this way lets me keep the same code between versions.
    private static final GsonBuilder PRETTY_GSON_BUILDER = new GsonBuilder().setLenient().setPrettyPrinting();
    private static Gson GSON_PRETTY;
    public static Gson getPretty(){
        return GSON_PRETTY;
    }

    /**
     * When not shadowing, please prefer using WrappedData#withGson or because that will ensure you do not conflict with other mods.
     * @param type the type of object that needs custom serialization handling
     * @param typeAdapter the type adapter that will handle serialization (instanceof TypeAdapter, JsonSerializer, JsonDeserializer, or InstanceCreator)
     */
    public static void addTypeAdapter(Type type, Object typeAdapter){
        GSON_BUILDER.registerTypeAdapter(type, typeAdapter);
        PRETTY_GSON_BUILDER.registerTypeAdapter(type, typeAdapter);
        init();
    }

    /**
     * When not shadowing, please prefer using WrappedData#withGson because that will ensure you do not conflict with other mods.
     */
    public static void addTypeAdapterFactory(TypeAdapterFactory factory){
        GSON_BUILDER.registerTypeAdapterFactory(factory);
        PRETTY_GSON_BUILDER.registerTypeAdapterFactory(factory);
        init();
    }

    private static void init(){
        GSON = GSON_BUILDER.create();
        GSON_PRETTY = PRETTY_GSON_BUILDER.create();
    }

    static {
        addTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer());
        addTypeAdapter(GenericHolder.class, new GenericHolder.TypeAdapter());
        addTypeAdapter(CompoundTag.class, new NbtTypeAdapter());
        addTypeAdapter(ItemStack.class, new ItemStackTypeAdapter());
        addTypeAdapter(BlockPos.class, new BlockPosTypeAdapter());
        addTypeAdapter(Vec3i.class, new BlockPosTypeAdapter());
        addTypeAdapterFactory(new RegistryObjectTypeAdapterFactory());
    }

    public static String safeFileName(String s){
        return s.toLowerCase(Locale.ROOT)
                .replace(":", "-")
                .replace(" ", "-");
    }
}
