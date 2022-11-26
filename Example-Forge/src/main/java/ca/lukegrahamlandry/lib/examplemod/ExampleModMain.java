package ca.lukegrahamlandry.lib.examplemod;

import ca.lukegrahamlandry.lib.config.ConfigWrapper;
import net.minecraftforge.fml.common.Mod;

@Mod("featurelib")
public class ExampleModMain {
    public static ConfigWrapper<ExampleConfig> config = new ConfigWrapper<>(ExampleConfig.class, "featurelib-example", ConfigWrapper.Side.SERVER);
}
