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
import ca.lukegrahamlandry.lib.data.impl.map.MapDataWrapper;
import ca.lukegrahamlandry.lib.network.ClientSideHandler;
import com.google.gson.JsonObject;

import java.util.Objects;

public class MultiMapDataSyncMessage implements ClientSideHandler {
    String value;
    String name;
    String dir;

    public MultiMapDataSyncMessage(MapDataWrapper<?, ?, ?> wrapper) {
        this.name = wrapper.getName();
        this.dir = wrapper.getSubDirectory();

        // encode here using ConfigWrapper#getGson instead of allowing the object to be encoded by the packet module's gson instance
        // this allows adding type adapters to your ConfigWrapper and still having syncing
        // TODO: since this is a Map<S, V> instead of a Map<S, GenericHolder<V>> the entries may not be subclasses of V since the type info will be lost in json conversion
        this.value = wrapper.getGson().toJson(wrapper.getMap());
    }

    public void handle() {
        boolean handled = false;
        for (DataWrapper<?> data : DataWrapper.ALL) {
            if (data instanceof MapDataWrapper<?, ?, ?> && Objects.equals(this.dir, data.getSubDirectory()) && data.getName().equals(this.name)) {
                JsonObject syncedValue = data.getGson().fromJson(this.value, JsonObject.class);
                ((MapDataWrapper<?, ?, ?>) data).loadFromMap(syncedValue);
                handled = true;
            }
        }

        if (!handled) throw new RuntimeException("received data sync for unknown {name: " + this.name + ", dir: " + this.dir + "}");
    }
}
