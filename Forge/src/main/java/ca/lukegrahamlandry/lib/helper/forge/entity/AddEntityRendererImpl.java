package ca.lukegrahamlandry.lib.helper.forge.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, value= Dist.CLIENT)
public class AddEntityRendererImpl {
    public static final List<RendererContainer<?>> renderers = new ArrayList<>();

    public static <E extends Entity> void add(Supplier<EntityType<? extends E>> type, EntityRendererProvider<E> renderer) {
        renderers.add(new RendererContainer<>(type, renderer));
    }

    @SubscribeEvent
    public static void onClientSetup(EntityRenderersEvent.RegisterRenderers event) {
        renderers.forEach((container) -> container.call(event::registerEntityRenderer));
    }

    private static class RendererContainer<E extends Entity> {
        final Supplier<EntityType<? extends E>> type;
        final EntityRendererProvider<E> renderer;
        private RendererContainer(Supplier<EntityType<? extends E>> type, EntityRendererProvider<E> renderer){
            this.type = type;
            this.renderer = renderer;
        }

        private void call(BiConsumer<EntityType<? extends E>, EntityRendererProvider<E>> action){
            action.accept(this.type.get(), this.renderer);
        }
    }
}
