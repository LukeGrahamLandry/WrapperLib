/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.examplemod.fabric;

import ca.lukegrahamlandry.examplemod.ExampleEventHandlers;
import ca.lukegrahamlandry.lib.event.fabric.WrapperLibClientInitializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class ExampleClientInit implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        new WrapperLibClientInitializer().onInitializeClient();
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> ExampleEventHandlers.drawOverlay(matrixStack));
    }
}
