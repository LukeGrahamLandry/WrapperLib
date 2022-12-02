/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.event.fabric;

import ca.lukegrahamlandry.lib.base.event.EventWrapper;
import ca.lukegrahamlandry.lib.base.event.IEventCallbacks;
import net.fabricmc.api.ClientModInitializer;

public class WrapperLibClientInitializer implements ClientModInitializer {
    /**
     * If you shadow WrapperLib you must manually call this method.
     */
    @Override
    public void onInitializeClient() {
        EventWrapper.get().forEach(IEventCallbacks::onClientSetup);
    }
}
