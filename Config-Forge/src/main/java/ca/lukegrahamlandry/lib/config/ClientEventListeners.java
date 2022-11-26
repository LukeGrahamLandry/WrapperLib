package ca.lukegrahamlandry.lib.config;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus= Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventListeners {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onServerStart(FMLClientSetupEvent event){
        EventCallbacks.onClientStart();
    }
}
