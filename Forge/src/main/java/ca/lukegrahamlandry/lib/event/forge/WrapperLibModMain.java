/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.event.forge;

import ca.lukegrahamlandry.lib.base.event.EventWrapper;
import ca.lukegrahamlandry.lib.base.event.IEventCallbacks;
import net.minecraftforge.fml.common.Mod;

/**
 * If you shadow WrapperLib you must exclude this class and manually call EventWrapper#init
 * Forge gets confused if there are extra classes in your jar with the @Mod annotation.
 */
@Mod("wrapperlib")
public class WrapperLibModMain {
    public WrapperLibModMain(){
        EventWrapper.init();
    }
}
