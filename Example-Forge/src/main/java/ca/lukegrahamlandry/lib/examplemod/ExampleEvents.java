package ca.lukegrahamlandry.lib.examplemod;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ExampleEvents {
    @SubscribeEvent
    public static void onJump(LivingEvent.LivingJumpEvent event){
        event.getEntity().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, ExampleModMain.config.get().speedLevel));
    }

    @SubscribeEvent
    public static void onJoin(PlayerEvent.PlayerLoggedInEvent event){
        if (event.getEntity().level.isClientSide()) return;
        event.getEntity().addItem(ExampleModMain.config.get().sword);
    }
}
