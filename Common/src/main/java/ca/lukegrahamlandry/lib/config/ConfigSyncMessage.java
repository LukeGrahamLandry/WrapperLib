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

import java.util.Objects;

public class ConfigSyncMessage implements ClientSideHandler {
    static Logger LOGGER = LoggerFactory.getLogger(ConfigSyncMessage.class);

    String value;
    String name;
    String dir;

    public ConfigSyncMessage(ConfigWrapper<?> wrapper){
        this.name = wrapper.getName();
        this.dir = wrapper.getSubDirectory();

        // encode here using ConfigWrapper#getGson instead of allowing the object to be encoded by the packet module's gson instance
        // this allows adding type adapters to your ConfigWrapper and still having syncing
        this.value = wrapper.getGson().toJson(wrapper.get());
    }

    public void handle(){
        boolean handled = false;
        for (ConfigWrapper<?> config : ConfigWrapper.ALL){
            if (config.side == ConfigWrapper.Side.SYNCED && Objects.equals(this.name, config.getName()) && Objects.equals(this.dir, config.getSubDirectory())) {
                try {
                    Object syncedValue = config.getGson().fromJson(this.value, config.actualType);
                    config.set(syncedValue);
                } catch (JsonSyntaxException e){
                    LOGGER.error("Failed to parse synced config " + this.name + " to " + config.actualType.getTypeName());
                    LOGGER.error("data: " + this.value);
                    e.printStackTrace();
                }
                handled = true;
            }
        }

        if (!handled) LOGGER.error("Received config sync for unknown {name: " + this.name + ", dir: " + this.dir + "}");
    }
}
