package ca.lukegrahamlandry.lib.data;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.LevelAccessor;

public class EventCallbacks {
    public static void onServerStart(MinecraftServer server){
        DataWrapper.server = server;
        DataWrapper.ALL.forEach((data) -> {
            if (data.shouldSave) data.load();
            if (data.shouldSync) data.sync();
        });
    }

    public static void onServerStop(MinecraftServer server){
        DataWrapper.server = null;
    }

    // TODO: we only have to save the data of the level being saved
    // TODO: players and global only overworld? does that work if you exit world from the nether? i think it says overworld stays loaded no matter what.
    public static void onWorldSave(LevelAccessor level){
        DataWrapper.ALL.forEach((data) -> {
            if (data.shouldSave && data.isDirty) data.save();
        });
    }

    public static void onPlayerLoginServer(ServerPlayer player){
        DataWrapper.ALL.forEach((data) -> {
            if (data.shouldSync) data.sync();
        });
    }
}
