package ca.lukegrahamlandry.lib.packets;

import ca.lukegrahamlandry.lib.base.event.IEventCallbacks;

public class ForgeEventCallbacks implements IEventCallbacks {
    @Override
    public void onInit(){
        ForgePacketRegistry.registerPacketChannel();
    }
}
