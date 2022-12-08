package ca.lukegrahamlandry.lib.keybind.forge;

import ca.lukegrahamlandry.lib.keybind.KeybindTickCallbacks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(bus= Mod.EventBusSubscriber.Bus.FORGE)
public class KeybindEventListeners {
    @SubscribeEvent
    public static void onServerWorldTick(TickEvent.PlayerTickEvent event){
        if (!event.player.level.isClientSide() && event.phase == TickEvent.Phase.END) KeybindTickCallbacks.onServerPlayerTick((ServerPlayer) event.player);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event){
        if (event.phase == TickEvent.Phase.END) KeybindTickCallbacks.onClientTick();
    }
}
