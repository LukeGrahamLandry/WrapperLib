package ca.lukegrahamlandry.lib.packets;

import net.minecraft.server.level.ServerPlayer;

public interface ServerboundHandler {
    void handle(ServerPlayer player);
}
