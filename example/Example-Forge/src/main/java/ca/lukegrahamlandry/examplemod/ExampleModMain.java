package ca.lukegrahamlandry.examplemod;

import ca.lukegrahamlandry.examplemod.model.ExampleClientConfig;
import ca.lukegrahamlandry.examplemod.model.ExampleConfig;
import ca.lukegrahamlandry.examplemod.model.KillTracker;
import ca.lukegrahamlandry.lib.config.ConfigWrapper;
import ca.lukegrahamlandry.lib.data.DataWrapper;
import ca.lukegrahamlandry.lib.data.PlayerDataWrapper;
import ca.lukegrahamlandry.lib.packets.ForgePacketRegistry;
import net.minecraftforge.fml.common.Mod;
import net.tslat.smartbrainlib.api.core.SmartBrain;

import java.util.function.Supplier;

@Mod("featurelib")
public class ExampleModMain {
    public static Supplier<ExampleConfig> config = ConfigWrapper.synced(ExampleConfig.class).named("featurelib-example");
    public static Supplier<ExampleClientConfig> clientConfig = ConfigWrapper.client(ExampleClientConfig.class);

    public static PlayerDataWrapper<KillTracker> kills = DataWrapper.player(KillTracker.class).synced().saved().dir("featurelib-examplemod").named("kills");

    public ExampleModMain(){
        System.out.println("helloworld " + SmartBrain.class.getName());
        ForgePacketRegistry.registerPacketChannel();
    }
}
