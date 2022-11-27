package ca.lukegrahamlandry.lib.data;

import ca.lukegrahamlandry.lib.data.EventCallbacks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus= Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventListeners {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onServerStart(ServerStartedEvent event){
        EventCallbacks.onServerStart(event.getServer());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onServerStop(ServerStoppedEvent event){
        EventCallbacks.onServerStop(event.getServer());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event){
        if (event.getEntity().level.isClientSide()) return;
        EventCallbacks.onPlayerLoginServer((ServerPlayer) event.getEntity());
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onWorldSave(LevelEvent.Save event){
        EventCallbacks.onWorldSave(event.getLevel());
    }
}
