package ca.lukegrahamlandry.lib.base.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;

public interface IEventCallbacks {
    default void onServerStart(MinecraftServer server) {}

    default void onServerStop(MinecraftServer server) {}

    default void onLevelSave(LevelAccessor level) {}

    default void onPlayerLogin(Player player) {}

    default void onClientSetup() {}

    // currently must be called in mod constructor if shadowing and excluding mod class because no event annotation for this
    default void onInit() {}

    default void onReloadCommand() {}
}
