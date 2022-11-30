/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class RegistryPlatform {
    @ExpectPlatform
    public static <T> void init(RegistryWrapper<T> wrapper){
        throw new AssertionError();
    }
}
