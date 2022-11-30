package ca.lukegrahamlandry.lib.base.services;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.LevelAccessor;

public interface IEventCallbacks {
    default void onServerStart(MinecraftServer server) {}

    default void onServerStop(MinecraftServer server) {}

    default void onWorldSave(LevelAccessor level) {}

    default void onPlayerLoginServer(ServerPlayer player) {}

    default void onClientSetup() {}

    default void onInit() {}
}
