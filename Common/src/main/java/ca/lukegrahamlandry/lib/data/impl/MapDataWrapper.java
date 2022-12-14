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
import ca.lukegrahamlandry.lib.data.impl.file.*;
import ca.lukegrahamlandry.lib.data.sync.FullMapDataSyncMessage;
import ca.lukegrahamlandry.lib.data.sync.SingleEntryMapDataSyncMessage;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        if (!this.shouldLazyLoad) this.fileHandler = new SplitFileHandler<>(this);
        return (W) this;
    }

    /**
     * This will cause the normal MapFileHandler#load method to not fire.
     * Instead, an entry will be loaded from disk when you request it with MapDataWrapper#get.
     * As usual, all dirty entries will be saved on MapFileHandler#save.
     * Implies MapFileHandler#splitFiles (save in dir/name/id.etx {data} instead of dir/name.etx {id: data}).
     */
    public <W extends MapDataWrapper<K, I, V>> W lazy(){
        this.fileHandler = new LazyFileHandler<>(this);
        this.shouldLazyLoad = true;
        return (W) this;
    }

    // API

    public V get(K key){
        return this.getById(this.keyToId(key));
    }

    /**
     * Same as setDirty() but only syncs data for the one instance that changed. Results in a smaller packet being sent.
     * If splitFiles or lazyLoaded only the files for the dirty entries will be written.
     */
    public void setDirty(K key){
        this.isDirty = true;
        this.dirtyEntries.add(keyToId(key));
        if (this.shouldSync) this.sync(key);
    }

    @Override
    public void setDirty() {
        super.setDirty();
        this.dirtyEntries.addAll(this.data.keySet());
    }

    /**
     * Resets the data for one entry to default values.
     */
    public void remove(K key){
        this.data.remove(this.keyToId(key));
        this.fileHandler.clear(this.keyToId(key));
        this.setDirty(key);

        // fileHandler#clear already deleted the file if relevent. the setDirty call will recreate the default object to sync it. dont mark entry dirty so we dont just write out the default object for no reason.
        this.dirtyEntries.remove(keyToId(key));
    }

    // IMPL

    public Map<I, V> data = new HashMap<>();
    private final Class<I> idClazz; // for json deserialization
    private MapFileHandler<K, I, V> fileHandler;
    private boolean shouldLazyLoad = false;
    private Set<I> dirtyEntries = new HashSet<>();
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
            this.logger.error("cannot call DataWrapper get (a) before server startup (b) on client if unsynced (c) on client before sync");
            return null;
        }

        // if key not found, try to load it if lazy, otherwise use default.
        if (!data.containsKey(id)) {
            if (this.shouldLazyLoad) ((LazyFileHandler<K, I, V>)this.fileHandler).load(id);
            if (!data.containsKey(id)) data.put(id, this.createDefaultInstance());
        }

        return data.get(id);
    }

    @Override
    public void save() {
        if (server == null) {
            String msg = "cannot call DataWrapper#save (a) after server shutdown (b) on client";
            this.logger.error(msg);
            throw new RuntimeException(msg);
        }
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

    @Override
    public void sync() {
        if (!this.shouldSync) this.logger.error("called DataWrapper#sync but shouldSync=false");
        else new FullMapDataSyncMessage(this).sendToAllClients();
    }

    public void sync(K key) {
        if (!this.shouldSync) this.logger.error("called DataWrapper#sync but shouldSync=false");
        else new SingleEntryMapDataSyncMessage(this, this.keyToId(key)).sendToAllClients();
    }

    public boolean isDirty(I id) {
        return this.dirtyEntries.contains(id);
    }

    @InternalUseOnly
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
        this.isLoaded = true;
    }

    @InternalUseOnly
    public void set(Object id, Object value) {
        this.data.put((I) id, (V) value);
        this.isLoaded = true;
    }

    @InternalUseOnly
    public Map<I, V> getMap() {
        return this.data;
    }
}
