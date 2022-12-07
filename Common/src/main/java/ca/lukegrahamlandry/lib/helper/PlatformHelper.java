package ca.lukegrahamlandry.lib.helper;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class PlatformHelper {
    /**
     * If this returns true, you may not class load anything in the net.minecraft.client package. 
     */
    @ExpectPlatform
    public static boolean isDedicatedServer(){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isDevelopmentEnvironment(){
        throw new AssertionError();
    }
}
