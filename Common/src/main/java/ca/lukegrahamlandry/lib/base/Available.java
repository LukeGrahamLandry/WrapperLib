/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.base;

public enum Available {
    NETWORK("ca.lukegrahamlandry.lib.network.NetworkWrapper"),
    DATA("ca.lukegrahamlandry.lib.data.DataWrapper"),
    CONFIG("ca.lukegrahamlandry.lib.config.ConfigWrapper"),
    ENTITY("ca.lukegrahamlandry.lib.entity.EntityHelper"),
    REGISTRY("ca.lukegrahamlandry.lib.registry.RegistryWrapper");

    private final String clazz;

    Available(String clazz){
        this.clazz = clazz;
    }

    public boolean get(){
        return canFindClass(this.clazz);
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
