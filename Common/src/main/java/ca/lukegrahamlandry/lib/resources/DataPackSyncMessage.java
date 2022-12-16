/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.resources;

import ca.lukegrahamlandry.lib.config.ConfigSyncMessage;
import ca.lukegrahamlandry.lib.config.ConfigWrapper;
import ca.lukegrahamlandry.lib.network.ClientSideHandler;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DataPackSyncMessage implements ClientSideHandler {
    static Logger LOGGER = LogManager.getLogger(DataPackSyncMessage.class);

    String value;
    String directory;

    public DataPackSyncMessage(ResourcesWrapper<?> wrapper){
        this.directory = wrapper.directory;

        // encode here using ResourcesWrapper#getGson instead of allowing the object to be encoded by the packet module's gson instance
        // this allows adding type adapters to your ResourcesWrapper and still having syncing
        this.value = wrapper.getGson().toJson(wrapper.data);
    }

    public void handle(){
        boolean handled = false;
        for (ResourcesWrapper<?> resources : ResourcesWrapper.ALL){
            if (resources.isServerSide && resources.shouldSync && Objects.equals(this.directory, resources.directory)) {
                try {
                    Type target = TypeToken.getParameterized(HashMap.class, ResourceLocation.class, resources.valueType.getRawType()).getType();
                    Map<ResourceLocation, ?> syncedValue = resources.getGson().fromJson(this.value, target);
                    resources.set(syncedValue);
                } catch (JsonSyntaxException e){
                    LOGGER.error("Failed to parse synced data resources " + this.directory + " to Map: ResourceLocation->" + resources.valueType.getType().getTypeName());
                    LOGGER.error("data: " + this.value);
                    e.printStackTrace();
                }
                handled = true;
            }
        }

        if (!handled) LOGGER.error("Received data pack sync for unknown {dir: " + this.directory + "}");
    }
}
