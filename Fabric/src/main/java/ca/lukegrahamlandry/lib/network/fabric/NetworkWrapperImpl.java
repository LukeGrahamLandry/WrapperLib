package ca.lukegrahamlandry.lib.network.fabric;

import ca.lukegrahamlandry.lib.base.GenericHolder;
import ca.lukegrahamlandry.lib.base.event.IEventCallbacks;
import ca.lukegrahamlandry.lib.base.json.JsonHelper;
import ca.lukegrahamlandry.lib.network.NetworkWrapper;
import com.google.gson.JsonElement;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class NetworkWrapperImpl implements IEventCallbacks {
    private static MinecraftServer SERVER;

    @Override
    public void onInit(){
        ServerPlayNetworking.registerGlobalReceiver(NetworkWrapper.ID, (server, player, handler, buf, responseSender) -> {
            GenericHolder<?> message = GenericHolder.decodeBytes(buf);
            server.execute(() -> {
                BiConsumer action = NetworkWrapper.getServerHandler(message.clazz);
                if (action != null) action.accept(player, message.value);
            });
        });
    }

    @Override
    public void onClientSetup() {
        ClientPlayNetworking.registerGlobalReceiver(NetworkWrapper.ID, (client, handler, buf, responseSender) -> {
            GenericHolder<?> message = GenericHolder.decodeBytes(buf);
            client.execute(() -> {
                Consumer action = NetworkWrapper.getClientHandler(message.clazz);
                if (action != null) action.accept(message.value);
            });
        });
    }

    @Override
    public void onServerStart(MinecraftServer server) {
        SERVER = server;
    }

    @Override
    public void onServerStop(MinecraftServer server) {
        SERVER = null;
    }

    // SENDING

    public static <T> void sendToClient(ServerPlayer player, T message){
        ServerPlayNetworking.send(player, NetworkWrapper.ID, new GenericHolder<>(message).encodeBytes(PacketByteBufs.empty()));
    }

    public static <T> void sendToServer(T message){
        ClientPlayNetworking.send(NetworkWrapper.ID, new GenericHolder<>(message).encodeBytes(PacketByteBufs.empty()));
    }

    public static <T> void sendToAllClients(T message){
        SERVER.getPlayerList().getPlayers().forEach((player) -> sendToClient(player, message));
    }
}
