/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.config;

import ca.lukegrahamlandry.lib.network.ClientSideHandler;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: check against subDirectory

public class ConfigSyncMessage implements ClientSideHandler {
    static Logger LOGGER = LoggerFactory.getLogger(ConfigSyncMessage.class.getPackageName());

    String value;
    String name;

    public ConfigSyncMessage(ConfigWrapper<?> wrapper){
        this.name = wrapper.name;

        // encode here using ConfigWrapper#getGson instead of allowing the object to be encoded by the packet module's gson instance
        // this allows adding type adapters to your ConfigWrapper and still having syncing
        this.value = wrapper.getGson().toJson(wrapper.get());
    }

    public void handle(){
        boolean handled = false;
        for (ConfigWrapper<?> config : ConfigWrapper.ALL){
            if (config.name.equals(this.name) && config.side == ConfigWrapper.Side.SYNCED) {
                try {
                    Object syncedValue = config.getGson().fromJson(this.value, config.clazz);
                    config.set(syncedValue);
                } catch (JsonSyntaxException e){
                    LOGGER.error("Failed to parse synced config " + this.name + " to " + config.clazz);
                    LOGGER.error("data: " + this.value);
                    e.printStackTrace();
                }
                handled = true;
            }
        }

        if (!handled) LOGGER.error("Received config sync for unknown name: " + this.name);
    }
}
