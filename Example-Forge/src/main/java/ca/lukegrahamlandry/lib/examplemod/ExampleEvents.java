package ca.lukegrahamlandry.lib.examplemod;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ExampleEvents {
    @SubscribeEvent
    public static void onJump(LivingEvent.LivingJumpEvent event){
        if (event.getEntity().level.isClientSide()) return;
        event.getEntity().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, ExampleModMain.config.get().speedLevel));
    }

    @SubscribeEvent
    public static void onJoin(PlayerEvent.PlayerLoggedInEvent event){
        if (event.getEntity().level.isClientSide()) return;
        event.getEntity().addItem(ExampleModMain.config.get().sword);
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event){
        if (event.getEntity().level.isClientSide()) return;
        Entity killer = event.getSource().getEntity();
        if (killer instanceof Player){
            if (event.getEntity() instanceof Player) ExampleModMain.kills.get((Player) killer).players++;
            else ExampleModMain.kills.get((Player) killer).mobs++;
            ExampleModMain.kills.setDirty();
        }
    }
}
