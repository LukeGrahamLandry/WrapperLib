package ca.lukegrahamlandry.lib.helper.forge;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLLoader;

public class PlatformHelperImpl {
    public static boolean isDedicatedServer(){
        return FMLLoader.getDist() == Dist.DEDICATED_SERVER;
    }

    public static boolean isDevelopmentEnvironment(){
        return !FMLLoader.isProduction();
    }
}
