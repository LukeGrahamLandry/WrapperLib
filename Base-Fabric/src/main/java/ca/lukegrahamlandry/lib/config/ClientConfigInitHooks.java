package ca.lukegrahamlandry.lib.config;

import net.fabricmc.api.ClientModInitializer;

public class ClientConfigInitHooks implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EventCallbacks.onClientStart();
    }
}
