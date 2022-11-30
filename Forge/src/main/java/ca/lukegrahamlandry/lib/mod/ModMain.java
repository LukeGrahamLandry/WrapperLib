package ca.lukegrahamlandry.lib.mod;

import ca.lukegrahamlandry.lib.base.event.EventWrapper;
import ca.lukegrahamlandry.lib.base.event.IEventCallbacks;
import net.minecraftforge.fml.common.Mod;

@Mod("wrapperlib")
public class ModMain {
    public ModMain(){
        EventWrapper.get().forEach(IEventCallbacks::onInit);
    }
}
