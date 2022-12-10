/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.helper.forge;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

public class PlatformHelperImpl {
    public static boolean isDedicatedServer(){
        return FMLLoader.getDist() == Dist.DEDICATED_SERVER;
    }

    public static boolean isDevelopmentEnvironment(){
        return !FMLLoader.isProduction();
    }


    public static boolean isModLoaded(String modid){
        return ModList.get().isLoaded(modid);
    }
}
