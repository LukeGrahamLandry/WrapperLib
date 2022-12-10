/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.data.impl.file;

import ca.lukegrahamlandry.lib.data.DataWrapper;
import ca.lukegrahamlandry.lib.data.impl.MapDataWrapper;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;

public class LazyFileHandler<K, I, V> extends SplitFileHandler<K, I, V> {
    public LazyFileHandler(MapDataWrapper<K, I, V> wrapper){
        super(wrapper);
    }

    /**
     * Nothing is loaded on start up, it must be done individually with the methods below.
     */
    @Override
    public void load() {

    }

    /**
     * loads the value of one entry from disk
     */
    public void load(I id) {
        // first world load. no data will be found
        if (!this.getFolderPath().toFile().exists()) this.getFolderPath().toFile().mkdirs();
        if (this.getFilePath(id).toFile().exists()) {
            try {
                Reader reader = Files.newBufferedReader(getFilePath(id));
                V value = this.wrapper.getGson().fromJson(reader, this.wrapper.clazz);
                reader.close();
                this.wrapper.data.put(id, value);
            } catch (IOException | JsonSyntaxException e) {
                this.wrapper.logger.error("failed to load data from " + DataWrapper.forDisplay(getFilePath(id)));
                e.printStackTrace();
            }
        }

        // if not exists or error reading the DataWrapper is responsible for setting the default value in the map
    }
}
