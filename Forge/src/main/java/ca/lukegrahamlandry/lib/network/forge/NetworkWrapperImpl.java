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
import ca.lukegrahamlandry.lib.base.json.JsonHelper;
import ca.lukegrahamlandry.lib.network.NetworkWrapper;
import com.google.gson.JsonElement;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NetworkWrapperImpl implements IEventCallbacks {
    public static SimpleChannel channel;
    static int i = 0;

    @Override
    public void onInit(){
        if (channel != null) return;
                                                // remember to increment this when I switch to EfficientNetworkSerializer
        channel = NetworkRegistry.newSimpleChannel(NetworkWrapper.ID, () -> "1.0", "1.0"::equals, "1.0"::equals);
        channel.registerMessage(i++, GenericHolder.class, GenericHolder::encodeBytes, GenericHolder::decodeBytes, NetworkWrapperImpl::handle);
    }

    // HANDLING

    public static void handle(GenericHolder<?> message, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> {
            if (context.get().getSender() == null) {
                Consumer action = NetworkWrapper.getClientHandler(message.clazz);
                if (action != null) action.accept(message.value);
            }
            else {
                BiConsumer action = NetworkWrapper.getServerHandler(message.clazz);
                if (action != null) action.accept(context.get().getSender(), message.value);
            }
        });
        context.get().setPacketHandled(true);
    }

    // SENDING

    public static <T> void sendToClient(ServerPlayer player, T message){
        channel.send(PacketDistributor.PLAYER.with(() -> player), new GenericHolder<>(message));
    }

    public static <T> void sendToServer(T message){
        NetworkWrapperImpl.channel.sendToServer(new GenericHolder<>(message));
    }

    public static <T> void sendToAllClients(T message){
        NetworkWrapperImpl.channel.send(PacketDistributor.ALL.noArg(), new GenericHolder<>(message));
    }
}
