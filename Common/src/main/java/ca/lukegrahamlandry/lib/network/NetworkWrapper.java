/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.network;

import ca.lukegrahamlandry.lib.base.GenericHolder;
import ca.lukegrahamlandry.lib.base.Services;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class NetworkWrapper {
    public static boolean DEBUG = true;
    private static final INetworkHelper NETWORK = Services.load(INetworkHelper.class);

    public static Logger LOGGER = LoggerFactory.getLogger("LukeGrahamLandry/WrapperLib Network");
    public static Map<String, BiConsumer<ServerPlayer, ?>> SERVER_BOUND_HANDLERS = new HashMap<>();
    public static Map<String, Consumer<?>> CLIENT_BOUND_HANDLERS = new HashMap<>();

    public static <T> void sendToClient(ServerPlayer player, T message){
        NETWORK.sendToClient(player, new GenericHolder<>(message));
    }

    public static <T> void sendToServer(T message){
        NETWORK.sendToServer(new GenericHolder<>(message));
    }

    public static <T> void sendToAllClients(T message){
        NETWORK.sendToAllClients(new GenericHolder<>(message));
    }

    public static <T> void registerClientHandler(Class<T> clazz, Consumer<T> handler){
        CLIENT_BOUND_HANDLERS.put(clazz.getName(), handler);
    }

    public static <T> void registerServerHandler(Class<T> clazz, BiConsumer<ServerPlayer, T> handler){
        SERVER_BOUND_HANDLERS.put(clazz.getName(), handler);
    }

    public static <T> Consumer<T> getClientHandler(Class<T> clazz){
        if (CLIENT_BOUND_HANDLERS.containsKey(clazz.getName())){
            return (Consumer<T>) CLIENT_BOUND_HANDLERS.get(clazz.getName());
        }

        if (ClientboundHandler.class.isAssignableFrom(clazz)){
            return (obj) -> ((ClientboundHandler) obj).handle();
        }

        LOGGER.error("no clientbound packet handler registered for " + clazz.getName());
        return null;
    }

    public static <T> BiConsumer<ServerPlayer, T> getServerHandler(Class<T> clazz){
        if (SERVER_BOUND_HANDLERS.containsKey(clazz.getName())){
            return (BiConsumer<ServerPlayer, T>) SERVER_BOUND_HANDLERS.get(clazz.getName());
        }

        if (ServerboundHandler.class.isAssignableFrom(clazz)){
            return (sender, msg) -> ((ServerboundHandler) msg).handle(sender);
        }

        LOGGER.error("no serverbound packet handler registered for " + clazz.getName());
        return null;
    }
}
