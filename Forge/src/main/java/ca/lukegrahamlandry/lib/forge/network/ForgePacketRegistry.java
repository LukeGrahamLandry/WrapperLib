/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.forge.network;

import ca.lukegrahamlandry.lib.base.GenericHolder;
import ca.lukegrahamlandry.lib.base.json.JsonHelper;
import ca.lukegrahamlandry.lib.network.NetworkWrapper;
import com.google.gson.JsonElement;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ForgePacketRegistry {
    public static SimpleChannel channel;
    static int i = 0;

    public static void registerPacketChannel(){
        if (channel != null){
            NetworkWrapper.LOGGER.error("ForgePacketRegistry#registerPacketChannel called twice");
            return;
        }
        channel = NetworkRegistry.newSimpleChannel(new ResourceLocation("wrapperlib", ForgePacketRegistry.class.getName().toLowerCase(Locale.US)), () -> "1.0", s -> true, s -> true);
        ForgePacketRegistry packet = new ForgePacketRegistry();
        channel.registerMessage(i++, GenericHolder.class, packet::encode, packet::decode, packet::handle);
    }

    // TODO: remove log spam before release

    public void encode(GenericHolder<?> message, FriendlyByteBuf buffer){
        JsonElement data = JsonHelper.GSON.toJsonTree(message);
        if (NetworkWrapper.DEBUG) NetworkWrapper.LOGGER.debug("encode " + data);
        buffer.writeUtf(data.toString());
    }

    public GenericHolder<?> decode(FriendlyByteBuf buffer){
        String data = buffer.readUtf();
        if (NetworkWrapper.DEBUG) NetworkWrapper.LOGGER.debug("decode " + data);
        return JsonHelper.GSON.fromJson(data, GenericHolder.class);
    }

    public void handle(GenericHolder<?> message, Supplier<NetworkEvent.Context> context){
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
}
