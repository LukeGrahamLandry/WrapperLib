/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.network.fabric;

import ca.lukegrahamlandry.lib.base.GenericHolder;
import ca.lukegrahamlandry.lib.base.event.IEventCallbacks;
import ca.lukegrahamlandry.lib.network.NetworkWrapper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class NetworkWrapperImpl implements IEventCallbacks {
    private static MinecraftServer SERVER;

    @Override
    public void onInit(){
        ServerPlayNetworking.registerGlobalReceiver(NetworkWrapper.ID, (server, player, handler, buf, responseSender) -> {
            GenericHolder<?> message = GenericHolder.decodeBytes(buf);
            server.execute(() -> NetworkWrapper.handleServerPacket(player, message));
        });
    }

    @Override
    public void onClientSetup() {
        ClientPlayNetworking.registerGlobalReceiver(NetworkWrapper.ID, (client, handler, buf, responseSender) -> {
            GenericHolder<?> message = GenericHolder.decodeBytes(buf);
            client.execute(() -> NetworkWrapper.handleClientPacket(message));
        });
    }

    @Override
    public void onServerStarting(MinecraftServer server) {
        SERVER = server;
    }

    @Override
    public void onServerStopped(MinecraftServer server) {
        SERVER = null;
    }

    public static <T> void sendToServer(T message){
        ClientPlayNetworking.send(NetworkWrapper.ID, new GenericHolder<>(message).encodeBytes(PacketByteBufs.empty()));
    }

    public static <T> void sendToClient(ServerPlayer player, T message){
        ServerPlayNetworking.send(player, NetworkWrapper.ID, new GenericHolder<>(message).encodeBytes(PacketByteBufs.empty()));
    }

    public static <T> void sendToAllClients(T message){
        SERVER.getPlayerList().getPlayers().forEach((player) -> sendToClient(player, message));
    }

    public static <T> void sendToTrackingClients(Entity entity, T message){
        PlayerLookup.tracking(entity).forEach((p) -> sendToClient(p, message));
    }
}
