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
    public static final ResourceLocation ID_CLIENT_BOUND = new ResourceLocation("wrapperlib", NetworkWrapper.class.getName().toLowerCase(Locale.US) + "_client");
    public static final ResourceLocation ID_SERVER_BOUND = new ResourceLocation("wrapperlib", NetworkWrapper.class.getName().toLowerCase(Locale.US) + "_server");


    @Override
    public void onInit(){
        ServerPlayNetworking.registerGlobalReceiver(ID_SERVER_BOUND, (server, player, handler, buf, responseSender) -> {
            GenericHolder<?> message = decode(buf);
            server.execute(() -> {
                BiConsumer action = NetworkWrapper.getServerHandler(message.clazz);
                if (action != null) action.accept(player, message.value);
            });
        });
    }

    @Override
    public void onClientSetup() {
        ClientPlayNetworking.registerGlobalReceiver(ID_CLIENT_BOUND, (client, handler, buf, responseSender) -> {
            GenericHolder<?> message = decode(buf);
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

    // ENCODING

    private static FriendlyByteBuf encode(GenericHolder<?> message) {
        FriendlyByteBuf buffer = PacketByteBufs.empty();
        JsonElement data = JsonHelper.GSON.toJsonTree(message);
        buffer.writeUtf(data.toString(), NetworkWrapper.MAX_CHARS);
        return buffer;
    }

    private static GenericHolder<?> decode(FriendlyByteBuf buffer) {
        String data = buffer.readUtf(NetworkWrapper.MAX_CHARS);
        return JsonHelper.GSON.fromJson(data, GenericHolder.class);
    }


    // SENDING

    public static <T> void sendToClient(ServerPlayer player, T message){
        ServerPlayNetworking.send(player, ID_CLIENT_BOUND, encode(new GenericHolder<>(message)));
    }

    public static <T> void sendToServer(T message){
        ClientPlayNetworking.send(ID_SERVER_BOUND, encode(new GenericHolder<>(message)));
    }

    public static <T> void sendToAllClients(T message){
        SERVER.getPlayerList().getPlayers().forEach((player) -> sendToClient(player, message));
    }
}
