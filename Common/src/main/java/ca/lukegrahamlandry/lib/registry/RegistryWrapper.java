/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Supplier;


public class RegistryWrapper<T> {
    public static <T> RegistryWrapper<T> of(Registry<T> vanillaRegistry, String modid){
        return new RegistryWrapper<>(vanillaRegistry, modid);
    }

    protected static List<RegistryWrapper<?>> ALL = new ArrayList<>();

    public final Registry<T> registry;
    public final String modid;
    private final Logger logger;
    private final Map<String, Supplier<T>> values = new HashMap<>();
    private int initCount = -1;
    protected boolean autoInit = false;
    public RegistryWrapper(Registry<T> vanillaRegistry, String modid) {
        this.registry = vanillaRegistry;
        this.modid = modid;
        String id = "LukeGrahamLandry/WrapperLib Registry:" + this.modid + "-" + vanillaRegistry.key().location();
        this.logger = LoggerFactory.getLogger(id);
        ALL.add(this);
    }

    /**
     * Enqueue an object to be registered.
     * @param name The location to register the object.
     * @param itemConstructor A supplier for your object. You cannot use a direct item instance because Forge is weird.
     * @return A supplier for your object that will only resolve after registration has been handled.
     */
    public Supplier<T> register(String name, Supplier<T> itemConstructor){
        ResourceLocation rl = new ResourceLocation(this.modid, name);
        values.put(name, itemConstructor);
        return () -> this.registry.get(rl);
    }

    public Set<Map.Entry<String, Supplier<T>>> entrySet(){
        return this.values.entrySet();
    }

    public RegistryWrapper<T> autoInit(){
        this.autoInit = true;
        return this;
    }

    /**
     * Passes all current values to the loader to be registered.
     * Should only be called once which must be after you've added all your objects (subsequent calls will be ignored).
     * Either:
     * - call this from your mod initializer after you register all your objects or
     *      - fabic mods are unordered so this won't work
     * - call autoInit() and ensure your containing class is loaded with all objects registered before IEventCallbacks#init fires
     */
    public void init(){
        if (this.entrySet().size() == 0) {
            this.logger.debug("no objects to register");
            return;
        }
        if (this.initCount != -1) {
            int newEntries = this.entrySet().size() - this.initCount;
            if (newEntries > 0) this.logger.error("RegistryWrapper#init called twice. {} new items will be ignored", newEntries);
            return;
        }

        this.logger.debug("registering {} objects", this.entrySet().size());
        RegistryPlatform.init(this);
        this.initCount = this.entrySet().size();
    }
}
