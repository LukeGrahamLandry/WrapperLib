/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.helper.forge;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class EntityHelperImpl {
    // REGISTER ATTRIBUTES IMPL

    public static final List<AttributeContainer> attributes = new ArrayList<>();
    public static void attributes(Supplier<EntityType<? extends LivingEntity>> type, Supplier<AttributeSupplier.Builder> builder) {
        attributes.add(new AttributeContainer(type, builder));
    }

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class CommonEvent {
        @SubscribeEvent
        public static void handleAttributeEvent(EntityAttributeCreationEvent event){
            attributes.forEach((container) -> event.put(container.type.get(), container.builder.get().build()));
        }
    }


    private static class AttributeContainer {
        final Supplier<EntityType<? extends LivingEntity>> type;
        final Supplier<AttributeSupplier.Builder> builder;
        private AttributeContainer(Supplier<EntityType<? extends LivingEntity>> type, Supplier<AttributeSupplier.Builder> builder){
            this.type = type;
            this.builder = builder;
        }
    }

    // REGISTER RENDERER IMPL

    public static final List<RendererContainer<?>> renderers = new ArrayList<>();
    public static <E extends Entity> void renderer(Supplier<EntityType<? extends E>> type, EntityRendererProvider<E> renderer) {
        renderers.add(new RendererContainer<>(type, renderer));
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

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, value= Dist.CLIENT)
    public static class ClientEvent {
        @SubscribeEvent
        public static void onClientSetup(EntityRenderersEvent.RegisterRenderers event) {
            renderers.forEach((container) -> container.call(event::registerEntityRenderer));
        }
    }

}
