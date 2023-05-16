/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.data.impl;

import ca.lukegrahamlandry.lib.base.InternalUseOnly;
import ca.lukegrahamlandry.lib.data.DataWrapper;
import ca.lukegrahamlandry.lib.data.sync.GlobalDataSyncMessage;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

public class GlobalDataWrapper<T> extends DataWrapper<T, GlobalDataWrapper<T>> implements Supplier<T> {
    T value;
    public GlobalDataWrapper(TypeToken<T> type) {
        super(type);
    }

    @Override
    @NotNull
    public T get() {
        this.require(this.value != null, "cannot call DataWrapper#get (a) before server startup (b) on client if unsynced");
        return this.value;
    }

    public boolean isLoaded(){
        return this.value == null;
    }

    /**
     * Resets the data to default values.
     */
    public void clear(){
        this.value = this.getDefaultValue();
        this.setDirty();
    }

    @Override
    public void load() {
        this.require(server != null, "cannot call DataWrapper#load (a) before server startup (b) on client if unsynced");

        if (!this.getFilePath().toFile().exists()) {
            // first world load. no data will be found
            this.value = this.getDefaultValue();
            return;
        }

        try {
            Reader reader = Files.newBufferedReader(this.getFilePath());
            this.value = this.getGson().fromJson(reader, this.getValueType());
            reader.close();
        } catch (IOException | JsonSyntaxException e) {
            String msg = "failed to load data from " + forDisplay(this.getFilePath()) + ". Using default instead.";
            this.getLogger().error(msg);
            e.printStackTrace();
            this.value = this.getDefaultValue();
        }
    }

    @Override
    public void save() {
        Path path = this.getFilePath();
        path.toFile().getParentFile().mkdirs();
        Gson pretty = this.getGsonPretty();
        String json = pretty.toJson(this.value);
        try {
            Files.write(path, json.getBytes());
            this.isDirty = false;
        } catch (IOException e) {
            this.getLogger().error("failed to write data to " + forDisplay(path));
        }
    }

    @Override
    public void sync() {
        this.require(this.shouldSync, "called DataWrapper#sync but shouldSync=false");
        new GlobalDataSyncMessage(this).sendToAllClients();
    }

    @Override
    public void forget() {
        this.value = null;
    }

    protected Path getFilePath(){
        Path path = server.getWorldPath(LevelResource.ROOT).resolve("data");
        if (this.getSubDirectory() != null) path = path.resolve(this.getSubDirectory());
        path = path.resolve(this.getName() + "." + this.fileExtension);
        return path;
    }

    @InternalUseOnly
    public void set(Object v){
        this.value = (T) v;
    }
}
