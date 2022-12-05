package ca.lukegrahamlandry.lib.fabric;


import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class PlatformHelperImpl {
    public static boolean isDedicatedServer(){
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
    }

    public static boolean isDevelopmentEnvironment(){
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
