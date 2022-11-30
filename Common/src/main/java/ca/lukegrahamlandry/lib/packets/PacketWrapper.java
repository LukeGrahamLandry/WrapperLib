package ca.lukegrahamlandry.lib.packets;

import ca.lukegrahamlandry.lib.base.GenericHolder;
import ca.lukegrahamlandry.lib.base.Services;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PacketWrapper {
    private static final INetworkHelper NETWORK = Services.load(INetworkHelper.class);

    public static Logger LOGGER = LoggerFactory.getLogger("LukeGrahamLandry/WrapperLib Packets");
    public static Map<String, BiConsumer<ServerPlayer, Object>> HANDLERS = new HashMap<>();

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
        HANDLERS.put(clazz.getName(), (sender, msg) -> handler.accept((T) msg));
    }

    public static <T> void registerServerHandler(Class<T> clazz, BiConsumer<ServerPlayer, T> handler){
        HANDLERS.put(clazz.getName(), (sender, msg) -> handler.accept(sender, (T) msg));
    }

    public static <T> Consumer<T> getClientHandler(Class<T> clazz){
        if (HANDLERS.containsKey(clazz.getName())){
            return (msg) -> HANDLERS.get(clazz.getName()).accept(null, msg);
        }

        if (ClientboundHandler.class.isAssignableFrom(clazz)){
            return (obj) -> ((ClientboundHandler) obj).handle();
        }

        LOGGER.error("no clientbound packet handler registered for " + clazz.getName());
        return null;
    }

    public static <T> BiConsumer<ServerPlayer, T> getServerHandler(Class<T> clazz){
        if (HANDLERS.containsKey(clazz.getName())){
            return (sender, msg) -> HANDLERS.get(clazz.getName()).accept(sender, msg);
        }

        if (ServerboundHandler.class.isAssignableFrom(clazz)){
            return (sender, msg) -> ((ServerboundHandler) msg).handle(sender);
        }

        LOGGER.error("no serverbound packet handler registered for " + clazz.getName());
        return null;
    }
}
