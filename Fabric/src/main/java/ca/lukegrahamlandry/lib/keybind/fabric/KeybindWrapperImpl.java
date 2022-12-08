package ca.lukegrahamlandry.lib.keybind.fabric;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

public class KeybindWrapperImpl {
    public static void register(KeyMapping key){
        KeyBindingHelper.registerKeyBinding(key);
    }
}
