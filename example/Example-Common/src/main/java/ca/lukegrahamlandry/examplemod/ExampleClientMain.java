/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.examplemod;

import ca.lukegrahamlandry.lib.helper.EntityHelper;
import net.minecraft.client.renderer.entity.DrownedRenderer;

public class ExampleClientMain {
    public static void init(){
        EntityHelper.renderer(RegistryTest.SOMETHING::get, DrownedRenderer::new);
    }
}
