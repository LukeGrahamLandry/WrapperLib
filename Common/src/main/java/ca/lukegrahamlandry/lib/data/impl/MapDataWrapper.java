/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.data.impl;

import ca.lukegrahamlandry.lib.data.DataWrapper;
import ca.lukegrahamlandry.lib.data.impl.file.MapFileHandler;
import ca.lukegrahamlandry.lib.data.impl.file.SingleFileHandler;
import ca.lukegrahamlandry.lib.data.impl.file.SplitFileHandler;
import ca.lukegrahamlandry.lib.data.sync.SplitMapDataSyncMessage;
import ca.lukegrahamlandry.lib.data.sync.SingleMapDataSyncMessage;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.util.HashMap;
import java.util.Map;

/**
 * @param <K> the user facing key object. ie. player or world
 * @param <I> the id of a key object. i.e. uuid or dimension resource location
 *           - must have a consistent hash value (bad: Player#hashCode uses Entity#id which depends on the order players joined the server that run)
 *           - there must exist a bijection between K and I (so i can recreate the list of K that have a value)
 *           - there must exist a bijection between String and I (using the toString method of I)
 * @param <V> the value to be stored
 */
public abstract class MapDataWrapper<K, I, V> extends DataWrapper<V> {
    /**
     * Save in dir/name/id.etx {data} instead of dir/name.etx {id: data}
     */
    public <W extends MapDataWrapper<K, I, V>> W splitFiles(){
        this.fileHandler = new SplitFileHandler<>(this);
        return (W) this;
    }

    // API

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


    // IMPL

    public Map<I, V> data = new HashMap<>();
    private final Class<I> idClazz; // for json deserialization
    private MapFileHandler fileHandler;
    protected MapDataWrapper(Class<I> idClazz, Class<V> clazz) {
        super(clazz);
        this.idClazz = idClazz;
        this.fileHandler = new SingleFileHandler<>(this);
    }

    public abstract I keyToId(K key);

    /**
     *
     * @param id the toString value of the original id object
     * @return a recreation of the original id object with the same hashcode
     */
    public abstract I stringToId(String id);

    public V getById(I id){
        if (!this.isLoaded) {
            this.logger.error("cannot call DataWrapper get (a) before server startup (b) on client if unsynced");
            return null;
        }
        if (!data.containsKey(id)) data.put(id, this.createDefaultInstance());
        return data.get(id);
    }

    @Override
    public void save() {
        this.fileHandler.save();
    }

    @Override
    public void load() {
        if (server == null) {
            String msg = "cannot call DataWrapper#load (a) before server startup (b) on client";
            this.logger.error(msg);
            throw new RuntimeException(msg);
        }

        this.fileHandler.load();
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
                this.logger.error("Ignoring key " + entry.getKey() + "; Failed to parse json data: " + entry.getValue());
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

        new SplitMapDataSyncMessage(this).sendToAllClients();
    }

    public void sync(K key) {
        if (!this.shouldSync) {
            this.logger.error("called DataWrapper#sync but shouldSync=false");
            return;
        }

        new SingleMapDataSyncMessage(this, this.keyToId(key)).sendToAllClients();
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
