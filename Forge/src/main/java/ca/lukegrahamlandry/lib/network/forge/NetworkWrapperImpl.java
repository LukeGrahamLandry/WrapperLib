/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.network.forge;

import ca.lukegrahamlandry.lib.base.GenericHolder;
import ca.lukegrahamlandry.lib.base.event.IEventCallbacks;
import ca.lukegrahamlandry.lib.network.NetworkWrapper;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class NetworkWrapperImpl implements IEventCallbacks {
    public static SimpleChannel channel;
    static int id = 0;

    @Override
    public void onInit(){
        if (channel != null) {
            NetworkWrapper.LOGGER.error("onInit called twice");
            return;
        }
                                                // remember to increment this when I switch to EfficientNetworkSerializer
        channel = NetworkRegistry.newSimpleChannel(NetworkWrapper.ID, () -> "1.0", "1.0"::equals, "1.0"::equals);
        channel.registerMessage(id++, GenericHolder.class, GenericHolder::encodeBytes, GenericHolder::decodeBytes, NetworkWrapperImpl::handle);
    }

    public static void handle(GenericHolder<?> message, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> {
            if (context.get().getSender() == null) NetworkWrapper.handleClientPacket(message);
            else NetworkWrapper.handleServerPacket(context.get().getSender(), message);
        });
        context.get().setPacketHandled(true);
    }

    public static <T> void sendToServer(T message){
        NetworkWrapperImpl.channel.sendToServer(new GenericHolder<>(message));
    }

    public static <T> void sendToClient(ServerPlayer player, T message){
        channel.send(PacketDistributor.PLAYER.with(() -> player), new GenericHolder<>(message));
    }

    public static <T> void sendToAllClients(T message){
        NetworkWrapperImpl.channel.send(PacketDistributor.ALL.noArg(), new GenericHolder<>(message));
    }

    public static <T> void sendToTrackingClients(Entity entity, T message){
        channel.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new GenericHolder<>(message));
    }

    public static <T> Packet<?> toVanillaPacket(T message, boolean isClientBound){
        return channel.toVanillaPacket(new GenericHolder<>(message), isClientBound ? NetworkDirection.PLAY_TO_CLIENT : NetworkDirection.PLAY_TO_SERVER);
    }
}
