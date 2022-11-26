package ca.lukegrahamlandry.lib.examplemod;

import ca.lukegrahamlandry.lib.config.ConfigWrapper;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Supplier;

@Mod("featurelib")
public class ExampleModMain {
    public static Supplier<ExampleConfig> config = ConfigWrapper.server(ExampleConfig.class).named("featurelib-example");
    public static Supplier<ExampleClientConfig> clientConfig = ConfigWrapper.client(ExampleClientConfig.class);
}
