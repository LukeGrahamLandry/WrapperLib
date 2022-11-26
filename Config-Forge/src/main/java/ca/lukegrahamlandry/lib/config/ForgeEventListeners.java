package ca.lukegrahamlandry.lib.config;

import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus= Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventListeners {
    @SubscribeEvent
    public static void onServerStart(ServerStartedEvent event){
        EventCallbacks.onServerStart(event.getServer());
    }
}
