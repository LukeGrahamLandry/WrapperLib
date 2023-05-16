package ca.lukegrahamlandry.lib.base;

import net.minecraft.client.Minecraft;

public class SafeClientHelper {
    public static Minecraft getMinecraft(){
        return Minecraft.getInstance();
    }
}
