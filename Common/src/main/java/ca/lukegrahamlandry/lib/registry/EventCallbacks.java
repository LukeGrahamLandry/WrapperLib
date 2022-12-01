/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.registry;

import ca.lukegrahamlandry.lib.base.event.IEventCallbacks;

public class EventCallbacks implements IEventCallbacks {
    // instead of doing this at the earliest possible time, it should be done at the latest possible time
    // can i mixin to when the registry gets frozen
    @Override
    public void onInit() {
        RegistryWrapper.ALL.forEach((wrapper) -> {
            if (wrapper.autoInit) wrapper.init();
        });
    }
}
