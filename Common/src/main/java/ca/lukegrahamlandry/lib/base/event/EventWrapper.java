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

    /**
     * I've just reinvented services for myself.
     * Having a file in META-INF with these class names doesn't solve my problem
     * because I want them to be optional. So if someone excluded the package with the class, it should just be ignored.
     * I could do this by having multiple gradle projects building multiple jars. Then people can just depend on the ones they want.
     * I did that for a while, but it just makes dealing with the project so much more complex for little benefit.
     * Only real disadvantage of this is that intellij doesn't auto handle it when I move or rename a class. The shadowJar relocate works fine tho.
     * Could also make an annotation processor to generate the list, but I don't want to deal with it right now.
     */
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
        add("ca.lukegrahamlandry.lib.network.forge.NetworkWrapperImpl");
        add("ca.lukegrahamlandry.lib.network.fabric.NetworkWrapperImpl");
        add("ca.lukegrahamlandry.lib.config.EventCallbacks");
        add("ca.lukegrahamlandry.lib.data.EventCallbacks");
        add("ca.lukegrahamlandry.lib.network.HandshakeHelper");
    }

    public static List<IEventCallbacks> get(){
        return HANDLERS;
    }
}
