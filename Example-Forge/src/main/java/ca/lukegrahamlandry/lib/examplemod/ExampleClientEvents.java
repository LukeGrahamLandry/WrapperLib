package ca.lukegrahamlandry.lib.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ExampleClientEvents {
    @SubscribeEvent
    public static void drawHud(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() == VanillaGuiOverlay.CROSSHAIR.type()){
            KillTracker kills = ExampleModMain.kills.get(Minecraft.getInstance().player);
            Minecraft.getInstance().font.draw(event.getPoseStack(), "Player Kills: " + kills.players, 20, 20, 0xFFFFFF);
            Minecraft.getInstance().font.draw(event.getPoseStack(), "Mob Kills: " + kills.mobs, 20, 40, 0xFFFFFF);
        }
    }
}
