/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.base;

/**
 * Check if certain modules are available. This allows stuff to fail gracefully if you exclude some parts while shadowing.
 */
public enum Available {
    NETWORK("ca.lukegrahamlandry.lib.network.NetworkWrapper"),
    DATA("ca.lukegrahamlandry.lib.data.DataWrapper"),
    CONFIG("ca.lukegrahamlandry.lib.config.ConfigWrapper"),
    REGISTRY("ca.lukegrahamlandry.lib.registry.RegistryWrapper"),
    KEYBIND("ca.lukegrahamlandry.lib.keybind.KeybindWrapper"),
    ENTITY_HELPER("ca.lukegrahamlandry.lib.helper.EntityHelper"),
    PLATFORM_HELPER("ca.lukegrahamlandry.lib.helper.PlatformHelper");

    private final String clazz;
    private Boolean memo;

    Available(String clazz){
        this.clazz = clazz;
    }

    public boolean get(){
        if (this.memo == null) this.memo = canFindClass(this.clazz);
        return this.memo;
    }

    public static boolean canFindClass(String className){
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
