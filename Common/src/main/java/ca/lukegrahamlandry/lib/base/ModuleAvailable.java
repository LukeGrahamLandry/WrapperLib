/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.base;

public class ModuleAvailable {
    public static boolean packets(){
        return canFindClass("ca.lukegrahamlandry.lib.network.NetworkWrapper");
    }

    public static boolean data(){
        return canFindClass("ca.lukegrahamlandry.lib.data.DataWrapper");
    }

    public static boolean config(){
        return canFindClass("ca.lukegrahamlandry.lib.config.ConfigWrapper");
    }

    public static boolean commentedJson(){
        return canFindClass("ca.lukegrahamlandry.lib.config.GenerateComments");
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
