/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.helper;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class PlatformHelper {
    /**
     * If this returns true, you may not class load anything in the net.minecraft.client package. 
     */
    @ExpectPlatform
    public static boolean isDedicatedServer(){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isDevelopmentEnvironment(){
        throw new AssertionError();
    }
}
