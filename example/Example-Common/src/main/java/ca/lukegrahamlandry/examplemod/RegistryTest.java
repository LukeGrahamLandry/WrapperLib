/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.examplemod;

import ca.lukegrahamlandry.examplemod.obj.TestEntity;
import ca.lukegrahamlandry.examplemod.obj.TestItem;
import ca.lukegrahamlandry.lib.registry.RegistryWrapper;
import net.minecraft.client.renderer.entity.DrownedRenderer;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

public class RegistryTest {
    public static final RegistryWrapper<Item> ITEMS = RegistryWrapper.create(Registry.ITEM, ExampleCommonMain.MOD_ID);
    public static final Supplier<Item> SMILE = ITEMS.register("smiley_face", () -> new TestItem(new Item.Properties().fireResistant()));

    public static final RegistryWrapper<Block> BLOCKS = RegistryWrapper.create(Registry.BLOCK, ExampleCommonMain.MOD_ID);
    public static final Supplier<Block> TEST = BLOCKS.register("test", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIRT))).withItem();

    public static final RegistryWrapper<EntityType<?>> ENTITY = RegistryWrapper.create(Registry.ENTITY_TYPE, ExampleCommonMain.MOD_ID);
    public static final Supplier<EntityType<TestEntity>> SOMETHING = ENTITY
            .register("thing", EntityType.Builder.of(TestEntity::new, MobCategory.MONSTER))
            .withAttributes(Drowned::createAttributes)
            .withRenderer(() -> DrownedRenderer::new)
            .withSpawnEgg(0xFF0000, 0x0000FF);
}
