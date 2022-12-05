package ca.lukegrahamlandry.lib.helper;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class PlatformHelper {
    @ExpectPlatform
    public static boolean isDedicatedServer(){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isDevelopmentEnvironment(){
        throw new AssertionError();
    }
}
