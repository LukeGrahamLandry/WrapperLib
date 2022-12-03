/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.data.impl.map;

import ca.lukegrahamlandry.lib.data.DataWrapper;
import ca.lukegrahamlandry.lib.data.sync.MultiMapDataSyncMessage;
import ca.lukegrahamlandry.lib.data.sync.SingleMapDataSyncMessage;
import ca.lukegrahamlandry.lib.network.NetworkWrapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

// TODO: switch this.useMultipleFiles to .lazyLoaded() and only load nessaisary if
// TODO: a MapDataWrapper with useMultipleFiles=false is really a GlobalDataWrapper it just so happens that im writting a map in there instead

// TODO: split into an interface!
// SingleMapDataWrapper<K, I, V> extends GlobalDataWrapper<Map<String, V>> implements MapDataWrapper<K, I, V>
// MultiMapDataWrapper<K, I, V> extends DataWrapper implements DataWrapper implements MapDataWrapper<K, I, V>

/**
 * @param <K> the user facing key object. ie. player or world
 * @param <I> the id of a key object. i.e. uuid or dimension resource location
 *           - must have a consistent hash value (bad: Player#hashCode uses Entity#id which depends on the order players joined the server that run)
 *           - there must exist a bijection between K and I (so i can recreate the list of K that have a value)
 *           - there must exist a bijection between String and I (using the toString method of I)
 * @param <V> the value to be stored
 */
public abstract class MapDataWrapper<K, I, V> extends DataWrapper<V> {
    ///// INIT

    /**
     * Save in dir/name/id.etx {data} instead of dir/name.etx {id: data}
     */
    public <W extends MapDataWrapper<K, I, V>> W splitFiles(){
        this.useMultipleFiles = true;
        return (W) this;
    }

    ///// API

    public V get(K key){
        return this.getById(this.keyToId(key));
    }

    /**
     * Same as setDirty() but only syncs data for the one instance that changed.
     * Results in a smaller packet being sent.
     * TODO: if useMultipleFiles=true we should only write the changed file as well. so i guess keep a Map I->Boolean isDirty
     */
    public void setDirty(K key){
        this.isDirty = true;
        if (this.shouldSync) this.sync(key);
    }


    ///// IMPL

    private boolean useMultipleFiles = false;
    protected Map<I, V> data = new HashMap<>();
    private final Class<I> idClazz; // for json deserialization
    protected MapDataWrapper(Class<I> idClazz, Class<V> clazz) {
        super(clazz);
        this.idClazz = idClazz;
    }

    public abstract I keyToId(K key);

    /**
     *
     * @param id the toString value of the orignal id object
     * @return a recreation of the original id object with the same hashcode
     */
    public abstract I stringToId(String id);

    protected Path getFilePath(){
        return this.getFilePath(null);
    }

    public V getById(I id){
        if (!this.isLoaded) {
            this.logger.error("cannot call DataWrapper get (a) before server startup (b) on client if unsynced");
            return null;
        }
        if (!data.containsKey(id)) data.put(id, this.createDefaultInstance());
        return data.get(id);
    }

    // Note: im concerned that gson writing maps uses toString instead of type adapters but it's fine for this
    @Override
    public void save() {
        Gson pretty = this.getGson().newBuilder().setPrettyPrinting().create();
        if (this.useMultipleFiles){
            this.data.forEach((key, value) -> {
                Path path = this.getFilePath(key);
                path.toFile().getParentFile().mkdirs();
                String json = pretty.toJson(value);
                try {
                    Files.write(path, json.getBytes());
                } catch (IOException e) {
                    this.logger.error("failed to write data to " + forDisplay(path));
                }
            });
        } else {
            Path path = this.getFilePath(null);
            path.toFile().getParentFile().mkdirs();
            String json = pretty.toJson(this.data);
            try {
                Files.write(path, json.getBytes());
            } catch (IOException e) {
                this.logger.error("failed to write data to " + forDisplay(path));
            }
        }
    }

    @Override
    public void load() {
        if (server == null) {
            String msg = "cannot call DataWrapper#load (a) before server startup (b) on client";
            this.logger.error(msg);
            throw new RuntimeException(msg);
        }

        Path path = this.getFilePath();
        if (!path.toFile().exists()) {
            // first world load. no data will be found
            this.isLoaded = true;
            return;
        }

        if (this.useMultipleFiles){
            if (path.toFile().isFile()) {
                String msg = "(useMultipleFiles=true) failed to load data from " + forDisplay(path) + " because it is a file not a directory";
                this.logger.error(msg);
                throw new RuntimeException(msg);
            }
            for (File file : path.toFile().listFiles()){
                try {
                    Reader reader = Files.newBufferedReader(file.toPath());
                    String filename = file.getName().substring(0, file.getName().lastIndexOf("."));
                    I id = this.stringToId(filename);
                    V value = this.getGson().fromJson(reader, this.clazz);
                    reader.close();
                    this.data.put(id, value);
                } catch (IOException | JsonSyntaxException e) {
                    this.logger.error("failed to load data from " + forDisplay(path));
                    e.printStackTrace();
                    throw new RuntimeException("failed to load data from " + forDisplay(path));
                }
            }
        } else {
            try {
                Reader reader = Files.newBufferedReader(path);
                JsonObject fileInfo = this.getGson().fromJson(reader, JsonObject.class);
                reader.close();
                this.loadFromMap(fileInfo);
            } catch (IOException | JsonSyntaxException e) {
                this.logger.error("failed to load data from " + forDisplay(path));
                e.printStackTrace();
                throw new RuntimeException("failed to load data from " + forDisplay(path));
            }
        }

        this.isLoaded = true;
    }

    // NEVER CALL THIS
    // its just for the syncing stuff
    // TODO: apionly annotation or whatever
    public void loadFromMap(JsonObject json){
        for (Map.Entry<String, JsonElement> entry : json.entrySet()){
            try {
                I id = this.stringToId(entry.getKey());
                V value = this.getGson().fromJson(entry.getValue(), this.clazz);
                this.data.put(id, value);
            } catch (JsonSyntaxException e){
                this.logger.error("failed to parse json data " + entry.getValue() + " ignoring key " + entry.getKey());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void sync() {
        if (!this.shouldSync) {
            this.logger.error("called DataWrapper#sync but shouldSync=false");
            return;
        }

        NetworkWrapper.sendToAllClients(new MultiMapDataSyncMessage(this));
    }

    public void sync(K key) {
        if (!this.shouldSync) {
            this.logger.error("called DataWrapper#sync but shouldSync=false");
            return;
        }

        NetworkWrapper.sendToAllClients(new SingleMapDataSyncMessage(this, this.keyToId(key)));
    }

    /**
     * useMultipleFiles=true, id==null  -> the folder where your files will be
     * useMultipleFiles=true, id!=null  -> the file where data for that id is stored
     * useMultipleFiles=false, id==null -> the file where all data is stored
     */
    protected Path getFilePath(I id){
        Path path = server.getWorldPath(LevelResource.ROOT).resolve("data");
        if (this.subDirectory != null) path = path.resolve(this.subDirectory);

        if (this.useMultipleFiles){
            path = path.resolve(this.name);
            if (id != null) path = path.resolve(id + "." + this.fileExtension);
        } else {
            path = path.resolve(this.name + "." + this.fileExtension);
        }

        return path;
    }

    // NEVER CALL THIS
    // its just for the syncing stuff
    // TODO: apionly annotation or whatever
    public void set(Object id, Object value) {
        this.data.put((I) id, (V) value);
    }

    // internal use only
    public Map<I, V> getMap() {
        return this.data;
    }
}
