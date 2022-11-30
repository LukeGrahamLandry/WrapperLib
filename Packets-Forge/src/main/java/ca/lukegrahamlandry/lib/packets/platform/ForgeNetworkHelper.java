package ca.lukegrahamlandry.lib.packets.platform;

import ca.lukegrahamlandry.lib.packets.ForgePacketRegistry;
import ca.lukegrahamlandry.lib.packets.GenericHolder;
import ca.lukegrahamlandry.lib.packets.platform.services.INetworkHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
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
