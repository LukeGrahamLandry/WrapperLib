/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.examplemod.model;

import ca.lukegrahamlandry.lib.config.Comment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.HashMap;
import java.util.Map;

public class ExampleConfig {
    @Comment("the amplifier of speed effect to give when an entity jumps")
    public int speedLevel = 2;
    @Comment("this is a number")
    public float something = 2.5F;
    @Comment("this is a string")
    public String hello = "world";

    @Comment("monsters")
    public Map<ResourceLocation, Integer> sizes = new HashMap<>();

    @Comment("this is an nbt tag that gets serialized correctly")
    public CompoundTag tag = new CompoundTag();

    @Comment("this is an enchanted sword to give players when they join the world")
    public ItemStack sword = new ItemStack(Items.DIAMOND_SWORD, 1);

    public ExampleConfig(){
        sizes.put(new ResourceLocation("minecraft:pig"), 10);

        tag.putString("hello", "world");
        tag.putInt("number", 5);
        CompoundTag temp = new CompoundTag();
        temp.putIntArray("test", new int[]{1, 2, 3, 4});
        tag.put("data", temp);

        Map<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantments.FIRE_ASPECT, 2);
        EnchantmentHelper.setEnchantments(enchants, sword);
    }
}
