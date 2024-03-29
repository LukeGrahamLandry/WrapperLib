/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.resources;

import ca.lukegrahamlandry.lib.base.Available;
import ca.lukegrahamlandry.lib.base.InternalUseOnly;
import ca.lukegrahamlandry.lib.base.json.JsonHelper;
import ca.lukegrahamlandry.lib.config.ConfigWrapper;
import ca.lukegrahamlandry.lib.helper.PlatformHelper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.util.*;

public class ResourcesWrapper<T> extends SimplePreparableReloadListener<Map<ResourceLocation, List<JsonElement>>> {
    static MinecraftServer server = null;

    /**
     * Load information from a data pack on the logical server.
     */
    public static <T> ResourcesWrapper<T> data(Class<T> clazz, String directory){
        return new ResourcesWrapper<>(TypeToken.get(clazz), directory, true);
    }

    /**
     * Load information from a resource pack on the logical client.
     */
    public static <T> ResourcesWrapper<T> assets(Class<T> clazz, String directory){
        return new ResourcesWrapper<>(TypeToken.get(clazz), directory, false);
    }

    public ResourcesWrapper<T> mergeWith(MergeRule<T> rule){
        this.mergeRule = rule;
        return this;
    }

    public ResourcesWrapper<T> onLoad(Runnable action){
        this.onLoadAction = action;
        return this;
    }

    public ResourcesWrapper<T> onReceiveSync(Runnable action){
        if (!this.shouldSync) throw new RuntimeException("ResourcesWrapper#onReceiveSync may only be called for synced data packs");
        this.onReceiveSyncAction = action;
        return this;
    }

    public ResourcesWrapper<T> synced(){
        if (!this.isServerSide) throw new RuntimeException("ResourcesWrapper#synced may only be called for data packs, NOT resource packs.");
        if (!Available.NETWORK.get()) throw new RuntimeException("Called ResourcesWrapper#synced but WrapperLib Network module is missing.");

        this.shouldSync = true;
        return this;
    }

    public ResourcesWrapper<T> withGson(Gson gson){
        this.gson = gson;
        return this;
    }

    // API

    public Set<Map.Entry<ResourceLocation, T>> entrySet(){
        return data.entrySet();
    }

    public T get(ResourceLocation id){
        if (data == null) return null;
        return data.get(id);
    }

    public boolean isLoaded(){
        return this.data != null;
    }

    // IMPL

    static List<ResourcesWrapper<?>> ALL = new ArrayList<>();
    TypeToken<T> valueType;
    public final String directory;
    public final boolean isServerSide;
    Map<ResourceLocation, T> data;
    Logger logger;
    protected String suffix = ".json";
    private Gson gson = JsonHelper.get();
    boolean shouldSync = false;
    protected Runnable onLoadAction = () -> {};
    protected Runnable onReceiveSyncAction = () -> {};
    protected MergeRule<T> mergeRule = (resources) -> resources.get(resources.size() - 1);  // TODO: make sure I guessed the priority order right
    public ResourcesWrapper(TypeToken<T> valueType, String directory, boolean isServerSide){
        this.valueType = valueType;
        this.directory = directory;
        this.isServerSide = isServerSide;
        if (!this.isServerSide && Available.PLATFORM_HELPER.get() && PlatformHelper.isDedicatedServer()) return;
        ALL.add(this);
        registerResourceListener(this);
    }

    public Gson getGson(){
        return this.gson;
    }

    /**
     * For each filename in our directory of each pack we retrieve the list of files.
     * Each file comes from a different pack. They all get added to a list under their resource location.
     * This map with all the entries from every pack gets returned and later passed to PackWrapper#apply.
     */
    @Override
    protected Map<ResourceLocation, List<JsonElement>> prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        Map<ResourceLocation, List<JsonElement>> results = new HashMap<>();
        for (Map.Entry<ResourceLocation, List<Resource>> entry : resourceManager.listResourceStacks(this.directory, file -> file.getPath().endsWith(suffix)).entrySet()) {
            String actualPath = entry.getKey().getPath().substring(this.directory.length() + 1, entry.getKey().getPath().length() - suffix.length());
            ResourceLocation id = new ResourceLocation(entry.getKey().getNamespace(), actualPath);
            List<JsonElement> values = new ArrayList<>();
            for (Resource resource : entry.getValue()) {
                try (BufferedReader reader = resource.openAsReader()) {
                    values.add(this.getGson().fromJson(reader, JsonElement.class));
                } catch (JsonSyntaxException e) {
                    this.logger.error("Failed to parse json for " + id + " from pack " + resource.sourcePackId());
                    e.printStackTrace();
                } catch (Exception e) {
                    this.logger.error("Failed to read " + id + " from pack " + resource.sourcePackId());
                    e.printStackTrace();
                }
            }
            if (!values.isEmpty()) results.put(id, values);
        }
        return results;
    }

    /**
     * For each resource location, all the json elements get parsed into our data type.
     * The resultant list is passed to the merge rule and its result is saved.
     */
    @Override
    protected void apply(Map<ResourceLocation, List<JsonElement>> resources, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        this.data = new HashMap<>();
        for (Map.Entry<ResourceLocation, List<JsonElement>> entry : resources.entrySet()){
            ResourceLocation id = entry.getKey();
            List<JsonElement> resourceStack = entry.getValue();
            List<T> values = new ArrayList<>();

            for (JsonElement json : resourceStack){
                values.add(this.getGson().fromJson(json, this.valueType.getType()));
            }

            T finalValue = this.mergeRule.merge(values);
            this.data.put(id, finalValue);
        }
        this.onLoadAction.run();
        if (this.shouldSync && server != null) new DataPackSyncMessage(this).sendToAllClients();
    }

    @InternalUseOnly
    void set(Object value) {
        this.data = (Map<ResourceLocation, T>) value;
        this.onReceiveSyncAction.run();
    }

    /**
     * Platform specific registration of a resource reload listener.
     */
    @ExpectPlatform
    public static void registerResourceListener(ResourcesWrapper<?> wrapper){
        throw new AssertionError();
    }
}
