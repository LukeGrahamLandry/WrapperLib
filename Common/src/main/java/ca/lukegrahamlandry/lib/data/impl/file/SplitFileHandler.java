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
import com.google.gson.JsonSyntaxException;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class SplitFileHandler<K, I, V> implements MapFileHandler<K, I, V> {
    protected final MapDataWrapper<K, I, V> wrapper;

    public SplitFileHandler(MapDataWrapper<K, I, V> wrapper){
        this.wrapper = wrapper;
    }

    @Override
    public void save() {
        Gson pretty = this.wrapper.getGson().newBuilder().setPrettyPrinting().create();
        this.wrapper.data.forEach((key, value) -> {
            if (!this.wrapper.isDirty(key)) return;

            Path path = this.getFilePath(key);
            path.toFile().getParentFile().mkdirs();
            String json = pretty.toJson(value);
            try {
                Files.write(path, json.getBytes());
            } catch (IOException e) {
                this.wrapper.logger.error("failed to write data to " + DataWrapper.forDisplay(path));
            }
        });
    }

    @Override
    public void load() {
        // first world load. no data will be found
        if (!this.getFolderPath().toFile().exists()) return;

        for (File file : getFolderPath().toFile().listFiles()){
            try {
                Reader reader = Files.newBufferedReader(file.toPath());
                String filename = file.getName().substring(0, file.getName().lastIndexOf("."));
                I id = this.wrapper.stringToId(filename);
                V value = this.wrapper.getGson().fromJson(reader, this.wrapper.clazz);
                reader.close();
                this.wrapper.data.put(id, value);
            } catch (IOException | JsonSyntaxException e) {
                this.wrapper.logger.error("failed to load data from " + DataWrapper.forDisplay(file.toPath()));
                e.printStackTrace();
            }
        }
    }

    @Override
    public void clear(I id) {
        getFilePath(id).toFile().delete();
    }

    public Path getFilePath(I id){
        return getFolderPath().resolve(id + "." + this.wrapper.fileExtension);
    }

    public Path getFolderPath(){
        Path path = DataWrapper.server.getWorldPath(LevelResource.ROOT).resolve("data");
        if (this.wrapper.getSubDirectory() != null) path = path.resolve(this.wrapper.getSubDirectory());
        return path;
    }
}
