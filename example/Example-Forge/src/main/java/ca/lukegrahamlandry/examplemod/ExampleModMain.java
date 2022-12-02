/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.examplemod;

import ca.lukegrahamlandry.examplemod.model.ExampleClientConfig;
import ca.lukegrahamlandry.examplemod.model.ExampleConfig;
import ca.lukegrahamlandry.examplemod.model.KillTracker;
import ca.lukegrahamlandry.lib.base.event.EventWrapper;
import ca.lukegrahamlandry.lib.base.event.IEventCallbacks;
import ca.lukegrahamlandry.lib.config.ConfigWrapper;
import ca.lukegrahamlandry.lib.data.DataWrapper;
import ca.lukegrahamlandry.lib.data.impl.map.PlayerDataWrapper;
import ca.lukegrahamlandry.lib.network.NetworkWrapper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import net.tslat.smartbrainlib.api.core.SmartBrain;

import java.util.function.Supplier;

@Mod("wrapperlibexamplemod")
public class ExampleModMain {
    public static Supplier<ExampleConfig> config = ConfigWrapper.synced(ExampleConfig.class).named("wrapperlib-example");
    public static Supplier<ExampleClientConfig> clientConfig = ConfigWrapper.client(ExampleClientConfig.class);

    public static PlayerDataWrapper<KillTracker> kills = DataWrapper.player(KillTracker.class).synced().saved().dir("wrapperlib-examplemod").named("kills");

    public ExampleModMain(){
        System.out.println("helloworld " + SmartBrain.class.getName());
        if (FMLLoader.isProduction()) EventWrapper.get().forEach(IEventCallbacks::onInit);
        NetworkWrapper.handshake("wrapperlib-example", "1");
    }
}
