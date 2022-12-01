/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.network;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A platform independent network implementation that allows sending objects between the client and the server without manually writing byte buffer serialization code.
 */
public class NetworkWrapper {
    // SENDING

    /**
     * Send a packet from the server to a specific player's client.
     * @param player the player to send a packet to
     * @param message the data to send to the client.
     * @param <T> the message class. It must either be registered with NetworkWrapper#registerClientHandler or implement ClientSideHandler
     */
    @ExpectPlatform
    public static <T> void sendToClient(ServerPlayer player, T message){
        throw new AssertionError();
    }

    /**
     * Send a packet from the client to the server.
     * @param message the data to send to the sever.
     * @param <T> the message class. It must either implement ServerSideHandler or be registered with NetworkWrapper#registerServerHandler
     */
    @ExpectPlatform
    public static <T> void sendToServer(T message){
        throw new AssertionError();
    }

    /**
     * Send a packet from the server to all connected clients.
     * @param message the data to send to the client.
     * @param <T> the message class. It must either be registered with NetworkWrapper#registerClientHandler or implement ClientSideHandler
     */
    @ExpectPlatform
    public static <T> void sendToAllClients(T message){
        throw new AssertionError();
    }

    // MANUALLY REGISTERED HANDLERS

    /**
     * This MUST be used if your handler uses code from the net.minecraft.client package.
     * If you DO NOT use ANY client specific classes, your packet message object may implement ClientSideHandler instead.
     * @param clazz the message class. The handler will be called when an object of this type is received through the network. The handler will only match EXACTLY this class name, no subtypes may be sent over the network.
     * @param handler the function to call when an object of type clazz is received on the client
     * @param <T> the message class
     */
    public static <T> void registerClientHandler(Class<T> clazz, Consumer<T> handler){
        CLIENT_BOUND_HANDLERS.put(clazz.getName(), handler);
    }

    /**
     * Using this is completely optional. Your packet message object may implement ServerSideHandler instead.
     * @param clazz the message class. The handler will be called when an object of this type is received through the network. The handler will only match EXACTLY this class name, no subtypes may be sent over the network.
     * @param handler the function to call when an object of type clazz is received on the server
     * @param <T> the message class
     */
    public static <T> void registerServerHandler(Class<T> clazz, BiConsumer<ServerPlayer, T> handler){
        SERVER_BOUND_HANDLERS.put(clazz.getName(), handler);
    }

    // IMPL

    public static <T> Consumer<T> getClientHandler(Class<T> clazz){
        if (CLIENT_BOUND_HANDLERS.containsKey(clazz.getName())){
            return (Consumer<T>) CLIENT_BOUND_HANDLERS.get(clazz.getName());
        }

        if (ClientSideHandler.class.isAssignableFrom(clazz)){
            return (obj) -> ((ClientSideHandler) obj).handle();
        }

        LOGGER.error("no clientbound packet handler registered for " + clazz.getName());
        return null;
    }

    public static <T> BiConsumer<ServerPlayer, T> getServerHandler(Class<T> clazz){
        if (SERVER_BOUND_HANDLERS.containsKey(clazz.getName())){
            return (BiConsumer<ServerPlayer, T>) SERVER_BOUND_HANDLERS.get(clazz.getName());
        }

        if (ServerSideHandler.class.isAssignableFrom(clazz)){
            return (sender, msg) -> ((ServerSideHandler) msg).handle(sender);
        }

        LOGGER.error("no serverbound packet handler registered for " + clazz.getName());
        return null;
    }

    public static Logger LOGGER = LoggerFactory.getLogger("LukeGrahamLandry/WrapperLib Network");
    public static Map<String, BiConsumer<ServerPlayer, ?>> SERVER_BOUND_HANDLERS = new HashMap<>();
    public static Map<String, Consumer<?>> CLIENT_BOUND_HANDLERS = new HashMap<>();
}
