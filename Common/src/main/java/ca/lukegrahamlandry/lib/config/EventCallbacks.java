package ca.lukegrahamlandry.lib.config;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class EventCallbacks {
    public static void onServerStart(MinecraftServer server){
        ConfigWrapper.server = server;
        ConfigWrapper.ALL.forEach((config) -> {
            if (config.side.inWorldDir){
                config.load();
                config.sync();
            }
        });
    }

    public static void onPlayerLoginServer(ServerPlayer player){
        ConfigWrapper.ALL.forEach((config) -> {
            if (config.side == ConfigWrapper.Side.SYNCED){
                // TODO: dont have to resync to all players, just the new one
                config.sync();
            }
        });
    }

    public static void onClientStart(){
        ConfigWrapper.ALL.forEach((config) -> {
            if (config.side == ConfigWrapper.Side.CLIENT){
                config.load();
            }
        });
    }

    // figure out dealing with client ones where the player might not have perms to use reload command
    public static void onReloadCommand(){
        ConfigWrapper.ALL.forEach((config) -> {
            if (config.reloadable){
                config.load();
                config.sync();
            }
        });
    }
}
