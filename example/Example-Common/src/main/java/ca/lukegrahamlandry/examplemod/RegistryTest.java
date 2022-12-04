/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.examplemod;

import ca.lukegrahamlandry.lib.registry.RegistryWrapper;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class RegistryTest {
    public static final RegistryWrapper<Item> ITEMS = RegistryWrapper.of(Registry.ITEM, ExampleCommonMain.MOD_ID);

    public static final Supplier<Item> SMILE = ITEMS.register("smiley_face", () -> new Item(new Item.Properties().fireResistant()));
}
