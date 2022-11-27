package ca.lukegrahamlandry.lib.examplemod;

import ca.lukegrahamlandry.lib.config.ConfigWrapper;
import ca.lukegrahamlandry.lib.data.DataWrapper;
import ca.lukegrahamlandry.lib.data.type.PlayerDataWrapper;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Supplier;

@Mod("featurelib")
public class ExampleModMain {
    public static Supplier<ExampleConfig> config = ConfigWrapper.synced(ExampleConfig.class).named("featurelib-example");
    public static Supplier<ExampleClientConfig> clientConfig = ConfigWrapper.client(ExampleClientConfig.class);

    public static PlayerDataWrapper<KillTracker> kills = DataWrapper.player(KillTracker.class).synced().saved();
}
