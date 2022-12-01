/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.forge.mod;

import ca.lukegrahamlandry.lib.base.event.EventWrapper;
import net.minecraftforge.fml.common.Mod;

@Mod("wrapperlib")
public class ModMain {
    public ModMain(){
        EventWrapper.triggerInit();
    }
}
