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
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class SingleFileHandler<K, I, V> implements MapFileHandler<K, I, V> {
    private final MapDataWrapper<K, I, V, ?> wrapper;

    public SingleFileHandler(MapDataWrapper<K, I, V, ?> wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public void save() {
        Gson pretty = this.wrapper.getGsonPretty();
        this.getFilePath().toFile().getParentFile().mkdirs();
        String json = pretty.toJson(this.wrapper.data);
        try {
            Files.write(this.getFilePath(), json.getBytes());
        } catch (IOException e) {
            this.wrapper.getLogger().error("failed to write data to " + DataWrapper.forDisplay(this.getFilePath()));
        }
    }

    @Override
    public void load() {
        // first world load. no data will be found
        if (!this.getFilePath().toFile().exists()) return;

        try {
            Reader reader = Files.newBufferedReader(this.getFilePath());
            JsonObject fileInfo = this.wrapper.getGson().fromJson(reader, JsonObject.class);
            reader.close();
            this.wrapper.loadFromMap(fileInfo);
        } catch (IOException | JsonSyntaxException e) {
            String msg = "failed to load data from " + DataWrapper.forDisplay(this.getFilePath());
            this.wrapper.getLogger().error(msg);
            e.printStackTrace();
        }
    }

    /**
     * since all data is stored in one file, the removed entries will be over written when the rest are saved.
     */
    @Override
    public void clear(I id) {
        // NO OP
    }

    public Path getFilePath(){
        Path path = DataWrapper.server.getWorldPath(LevelResource.ROOT).resolve("data");
        if (this.wrapper.getSubDirectory() != null) path = path.resolve(this.wrapper.getSubDirectory());
        path = path.resolve(this.wrapper.getName() + "." + this.wrapper.fileExtension);
        return path;
    }
}
