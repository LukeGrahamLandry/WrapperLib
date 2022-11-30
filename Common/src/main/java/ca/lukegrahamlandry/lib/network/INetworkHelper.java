package ca.lukegrahamlandry.lib.network;

import ca.lukegrahamlandry.lib.base.GenericHolder;
import net.minecraft.server.level.ServerPlayer;

public interface INetworkHelper {
    void sendToClient(ServerPlayer player, GenericHolder<?> message);
    void sendToServer(GenericHolder<?> message);
    void sendToAllClients(GenericHolder<?> message);
}
