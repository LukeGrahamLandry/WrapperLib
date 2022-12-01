package ca.lukegrahamlandry.lib.event.fabric;

import ca.lukegrahamlandry.lib.base.event.EventWrapper;
import ca.lukegrahamlandry.lib.base.event.IEventCallbacks;
import net.fabricmc.api.ClientModInitializer;

public class WrapperLibClientInitializer implements ClientModInitializer {
    /**
     * If you shadow WrapperLib you must manually call this method.
     */
    @Override
    public void onInitializeClient() {
        EventWrapper.get().forEach(IEventCallbacks::onClientSetup);
    }
}
