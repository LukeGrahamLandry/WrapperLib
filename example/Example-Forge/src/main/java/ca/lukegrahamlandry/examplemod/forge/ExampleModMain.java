/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.examplemod.forge;

import ca.lukegrahamlandry.examplemod.ExampleCommonMain;
import ca.lukegrahamlandry.examplemod.model.ExampleClientConfig;
import ca.lukegrahamlandry.examplemod.model.ExampleConfig;
import ca.lukegrahamlandry.examplemod.model.KillTracker;
import ca.lukegrahamlandry.lib.base.event.EventWrapper;
import ca.lukegrahamlandry.lib.base.event.IEventCallbacks;
import ca.lukegrahamlandry.lib.config.ConfigWrapper;
import ca.lukegrahamlandry.lib.data.DataWrapper;
import ca.lukegrahamlandry.lib.data.impl.PlayerDataWrapper;
import ca.lukegrahamlandry.lib.network.NetworkWrapper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.function.Supplier;

@Mod(ExampleCommonMain.MOD_ID)
public class ExampleModMain {
    public ExampleModMain(){
        if (FMLLoader.isProduction()) EventWrapper.init();
        ExampleCommonMain.init();
    }
}
