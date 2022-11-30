package ca.lukegrahamlandry.lib.network;

import net.minecraft.server.level.ServerPlayer;

public interface ServerboundHandler {
    void handle(ServerPlayer player);
}
