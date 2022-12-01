/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.base.event;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class EventWrapper {
    private static List<IEventCallbacks> HANDLERS = new ArrayList<>();

    public static void add(IEventCallbacks handler){
        HANDLERS.add(handler);
    }

    public static void add(String handlerClassName){
        try {
            Class<?> clazz = Class.forName(handlerClassName);
            add((IEventCallbacks) clazz.getConstructor().newInstance());
        } catch (ClassNotFoundException ignored) {
            // dev used shadow exclude, that's fine
        }
        catch (ClassCastException e) {
            throw new RuntimeException(handlerClassName + " must implement IEventCallbacks", e);
        }
        catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(handlerClassName + " does not have a public parameterless constructor", e);
        }
    }

    static {
        add("ca.lukegrahamlandry.lib.base.EventCallbacks");
        add("ca.lukegrahamlandry.lib.forge.network.ForgeEventCallbacks");
        add("ca.lukegrahamlandry.lib.fabric.network.FabricEventCallbacks");
        add("ca.lukegrahamlandry.lib.config.EventCallbacks");
        add("ca.lukegrahamlandry.lib.data.EventCallbacks");
        add("ca.lukegrahamlandry.lib.registry.EventCallbacks");
    }

    public static List<IEventCallbacks> get(){
        return HANDLERS;
    }

    /**
     * Sends the mod init event to all tracking IEventCallbacks.
     * If you are shading you must call this in your mod initializer after setting up all your Wrappers.
     */
    public static void triggerInit(){
        EventWrapper.get().forEach(IEventCallbacks::onInit);
    }
}
