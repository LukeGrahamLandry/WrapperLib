/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.resources.forge;

import ca.lukegrahamlandry.lib.resources.ResourcesWrapper;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;


@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE)
public class ResourcesWrapperImpl {
    public static void registerResourceListener(ResourcesWrapper<?> wrapper){
        (wrapper.isServerSide ? SERVER_LISTENERS : ClientListenersEventListener.CLIENT_LISTENERS).add(wrapper);
    }

    private static final List<ResourcesWrapper<?>> SERVER_LISTENERS = new ArrayList<>();

    @SubscribeEvent
    public static void registerServerListener(AddReloadListenerEvent event){
        SERVER_LISTENERS.forEach(event::addListener);
    }
}
