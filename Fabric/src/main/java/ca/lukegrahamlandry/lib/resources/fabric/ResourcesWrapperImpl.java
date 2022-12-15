/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.resources.fabric;

import ca.lukegrahamlandry.lib.resources.ResourcesWrapper;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ResourcesWrapperImpl {
    public static void registerResourceListener(ResourcesWrapper<?> wrapper){
        ResourceManagerHelper.get(wrapper.isServerSide ? PackType.SERVER_DATA : PackType.CLIENT_RESOURCES).registerReloadListener(new Listener(wrapper));
    }

    private static class Listener implements IdentifiableResourceReloadListener {
        private final ResourcesWrapper<?> wrapper;

        private Listener(ResourcesWrapper<?> wrapper){
            this.wrapper = wrapper;
        }

        @Override
        public ResourceLocation getFabricId() {
            return new ResourceLocation("wrapperlib", this.wrapper.directory);
        }

        @Override
        public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller2, Executor executor, Executor executor2) {
            return wrapper.reload(preparationBarrier, resourceManager, profilerFiller, profilerFiller2, executor, executor2);
        }
    }
}
