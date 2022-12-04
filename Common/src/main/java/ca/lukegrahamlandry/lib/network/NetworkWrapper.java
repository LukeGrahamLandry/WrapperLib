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
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A platform independent network implementation that allows sending data between the client and the server without manually writing byte buffer serialization code.
 */
public class NetworkWrapper {
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
     * Send a packet from the server to a specific player's client.
     * @param player the player to send a packet to
     * @param message the data to send to the client.
     * @param <T> the message class. It must either implement ClientSideHandler or be registered with NetworkWrapper#registerClientHandler
     */
    @ExpectPlatform
    public static <T> void sendToClient(ServerPlayer player, T message){
        throw new AssertionError();
    }

    /**
     * Send a packet from the server to all connected clients.
     * @param message the data to send to the client.
     * @param <T> the message class. It must either implement ClientSideHandler or be registered with NetworkWrapper#registerClientHandler
     */
    @ExpectPlatform
    public static <T> void sendToAllClients(T message){
        throw new AssertionError();
    }

    /**
     * Using this is completely optional. Your packet message object may implement ClientSideHandler instead.
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

    /**
     * Allow WrapperLib to confirm that the client and server are running compatible versions of your mod.
     * This will be checked when a client joins a server. It will only be checked if the mod is present on both sides.
     * The client will be disconnected if either checkVersion predicate returns false.
     *
     * @param modid a unique identifier for your mod.
     * @param version your network protocol version.
     * @param clientCheckVersion called when a HandshakeMessage is received on the client. The parameter is the server's version.
     * @param serverCheckVersion called when a HandshakeMessage is received on the server. The parameter is the client's version.
     */
    public static void handshake(String modid, String version, Predicate<String> clientCheckVersion, Predicate<String> serverCheckVersion){
        HandshakeHelper.add(new HandshakeHelper.ModProtocol(modid, version));
        HandshakeHelper.CLIENT_VERSION_CHECKERS.put(modid, clientCheckVersion);
        HandshakeHelper.SERVER_VERSION_CHECKERS.put(modid, serverCheckVersion);
    }

    /**
     * Register the active version of your mod's network protocol.
     * Clients will only be allowed to connect to servers with equal versions.
     * You should change your version whenever you make a breaking change to objects that will be passed over the network.
     * For example, if you rename one of your message classes, trying to communicate with an old version will fail.
     * This method allows you to fail quickly with a clear error message rather than only when the changed packet gets sent.
     *
     * @param modid a unique identifier for your mod.
     * @param version your current network protocol version.
     */
    public static void handshake(String modid, String version){
        handshake(modid, version, version::equals, version::equals);
    }

    // IMPL

    public static <T> Consumer<T> getClientHandler(Class<T> clazz){
        if (CLIENT_BOUND_HANDLERS.containsKey(clazz.getName())){
            return (Consumer<T>) CLIENT_BOUND_HANDLERS.get(clazz.getName());
        }

        if (ClientSideHandler.class.isAssignableFrom(clazz)){
            return (obj) -> ((ClientSideHandler) obj).handle();
        }

        return null;
    }

    public static <T> BiConsumer<ServerPlayer, T> getServerHandler(Class<T> clazz){
        if (SERVER_BOUND_HANDLERS.containsKey(clazz.getName())){
            return (BiConsumer<ServerPlayer, T>) SERVER_BOUND_HANDLERS.get(clazz.getName());
        }

        if (ServerSideHandler.class.isAssignableFrom(clazz)){
            return (sender, msg) -> ((ServerSideHandler) msg).handle(sender);
        }

        return null;
    }

    public static <T> boolean handleServerPacket(ServerPlayer player, GenericHolder<T> message){
        BiConsumer<ServerPlayer, T> action = NetworkWrapper.getServerHandler(message.clazz);
        if (action == null) {
            LOGGER.error("No server bound packet handler registered for " + message.clazz.getName());
            return false;
        }

        try {
            action.accept(player, message.value);
            return true;
        } catch (RuntimeException e){
            LOGGER.error("Failed to handle packet: " + message.clazz);
            LOGGER.error("data: " + message.value);
            e.printStackTrace();
            return false;
        }
    }

    public static <T> boolean handleClientPacket(GenericHolder<T> message){
        Consumer<T> action = NetworkWrapper.getClientHandler(message.clazz);
        if (action == null) {
            LOGGER.error("No client bound packet handler registered for " + message.clazz.getName());
            return false;
        }

        try {
            action.accept(message.value);
            return true;
        } catch (RuntimeException e){
            LOGGER.error("Failed to handle packet: " + message.clazz);
            LOGGER.error("data: " + message.value);
            e.printStackTrace();
            return false;
        }
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(NetworkWrapper.class.getPackageName());
    public static final Map<String, BiConsumer<ServerPlayer, ?>> SERVER_BOUND_HANDLERS = new HashMap<>();
    public static final Map<String, Consumer<?>> CLIENT_BOUND_HANDLERS = new HashMap<>();

    /**
     * If WrapperLib is shadowed, there will be multiple versions of this class and its Impl that try to register packet handlers.
     * I need to use a different id for each and the package name will be unique because people must relocate into their package.
     * This way each mod's version of this class will receive only the packets it sent.
     */
    public static final ResourceLocation ID = new ResourceLocation("wrapperlib", NetworkWrapper.class.getPackageName());
}
