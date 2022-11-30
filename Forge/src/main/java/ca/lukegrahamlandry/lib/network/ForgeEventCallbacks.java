package ca.lukegrahamlandry.lib.network;

import ca.lukegrahamlandry.lib.base.event.IEventCallbacks;

public class ForgeEventCallbacks implements IEventCallbacks {
    @Override
    public void onInit(){
        ForgePacketRegistry.registerPacketChannel();
    }
}
