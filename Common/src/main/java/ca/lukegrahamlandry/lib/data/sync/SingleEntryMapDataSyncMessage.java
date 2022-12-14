/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.data.sync;

import ca.lukegrahamlandry.lib.data.DataWrapper;
import ca.lukegrahamlandry.lib.data.impl.MapDataWrapper;
import ca.lukegrahamlandry.lib.network.ClientSideHandler;

import java.util.Objects;

public class SingleEntryMapDataSyncMessage implements ClientSideHandler {
    String value;
    String id;
    String name;
    String dir;

    public <I> SingleEntryMapDataSyncMessage(MapDataWrapper<?, I, ?> wrapper, I id) {
        this.name = wrapper.getName();
        this.dir = wrapper.getSubDirectory();
        this.id = id.toString();

        // encode here using ConfigWrapper#getGson instead of allowing the object to be encoded by the packet module's gson instance
        // this allows adding type adapters to your ConfigWrapper and still having syncing
        // TODO: since not using a GenericHolder, data may not be a subclass of V
        this.value = wrapper.getGson().toJson(wrapper.getById(id));
    }

    public void handle() {
        boolean handled = false;
        for (DataWrapper<?> data : DataWrapper.ALL) {
            if (data instanceof MapDataWrapper<?, ?, ?> && Objects.equals(this.dir, data.getSubDirectory()) && data.getName().equals(this.name)) {
                Object syncedValue = data.getGson().fromJson(this.value, data.clazz);
                Object syncedID = ((MapDataWrapper<?, ?, ?>) data).stringToId(this.id);
                ((MapDataWrapper<?, ?, ?>) data).set(syncedID, syncedValue);
                handled = true;
            }
        }

        if (!handled) DataWrapper.LOGGER.error("SingleMap. Received data sync for unknown {name: " + this.name + ", dir: " + this.dir + "}");
    }
}
