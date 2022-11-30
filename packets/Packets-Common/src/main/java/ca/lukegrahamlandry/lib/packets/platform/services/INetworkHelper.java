package ca.lukegrahamlandry.lib.packets.platform.services;

import ca.lukegrahamlandry.lib.base.GenericHolder;
import net.minecraft.server.level.ServerPlayer;

public interface INetworkHelper {
    void sendToClient(ServerPlayer player, GenericHolder<?> message);
    void sendToServer(GenericHolder<?> message);
    void sendToAllClients(GenericHolder<?> message);
}
