package ca.lukegrahamlandry.lib.config;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class ConfigInitHooks implements ModInitializer {
    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register((EventCallbacks::onServerStart));
        ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> EventCallbacks.onPlayerLoginServer(handler.player)));
    }
}
