/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.helper.forge.entity;

import ca.lukegrahamlandry.lib.helper.EntityHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, value= Dist.CLIENT)
public class AddEntityRendererImpl {
    public static final List<RendererContainer<?>> renderers = new ArrayList<>();

    public static <E extends Entity> void add(Supplier<EntityType<? extends E>> type, EntityHelper.EntityRendererProvider<E> renderer) {
        renderers.add(new RendererContainer<>(type, renderer));
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        renderers.forEach((container) -> container.call(RenderingRegistry::registerEntityRenderingHandler));
    }

    private static class RendererContainer<E extends Entity> {
        final Supplier<EntityType<? extends E>> type;
        final EntityHelper.EntityRendererProvider<E> renderer;
        private RendererContainer(Supplier<EntityType<? extends E>> type, EntityHelper.EntityRendererProvider<E> renderer){
            this.type = type;
            this.renderer = renderer;
        }

        private void call(BiConsumer<EntityType<? extends E>, IRenderFactory<E>> action){
            action.accept(this.type.get(), this.renderer::create);
        }
    }
}
