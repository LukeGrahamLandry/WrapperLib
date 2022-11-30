package ca.lukegrahamlandry.lib.network;

import ca.lukegrahamlandry.lib.base.GenericHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public class ForgeNetworkHelper implements INetworkHelper {
    @Override
    public void sendToClient(ServerPlayer player, GenericHolder<?> message) {
        ForgePacketRegistry.channel.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    @Override
    public void sendToServer(GenericHolder<?> message) {
        ForgePacketRegistry.channel.sendToServer(message);
    }

    @Override
    public void sendToAllClients(GenericHolder<?> message) {
        ForgePacketRegistry.channel.send(PacketDistributor.ALL.noArg(), message);
    }
}
