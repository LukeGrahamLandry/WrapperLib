package ca.lukegrahamlandry.lib.packets;

import ca.lukegrahamlandry.lib.base.GenericHolder;
import ca.lukegrahamlandry.lib.packets.ForgePacketRegistry;
import ca.lukegrahamlandry.lib.packets.INetworkHelper;
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
