/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.helper.fabric;


import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class PlatformHelperImpl {
    public static boolean isDedicatedServer(){
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
    }

    public static boolean isDevelopmentEnvironment(){
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    public static boolean isModLoaded(String modid){
        return FabricLoader.getInstance().isModLoaded(modid);
    }
}
