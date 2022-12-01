package ca.lukegrahamlandry.lib.event.fabric;

import ca.lukegrahamlandry.lib.base.event.EventWrapper;
import ca.lukegrahamlandry.lib.base.event.IEventCallbacks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class WrapperLibModInitializer implements ModInitializer {
    /**
     * If you shadow WrapperLib you must manually call this method.
     */
    @Override
    public void onInitialize() {
        EventWrapper.get().forEach(IEventCallbacks::onInit);

        ServerLifecycleEvents.SERVER_STARTED.register(server -> EventWrapper.get().forEach((event) -> event.onServerStart(server)));
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> EventWrapper.get().forEach((event) -> event.onServerStop(server)));
        ServerWorldEvents.UNLOAD.register((server, world) -> EventWrapper.get().forEach((event) -> event.onLevelSave(world)));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> EventWrapper.get().forEach((event) -> event.onPlayerLogin(handler.getPlayer())));
    }
}
