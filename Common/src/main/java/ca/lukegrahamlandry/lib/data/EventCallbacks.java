/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.data;

import ca.lukegrahamlandry.lib.base.event.IEventCallbacks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;

public class EventCallbacks implements IEventCallbacks {
    @Override
    public void onServerStart(MinecraftServer server){
        DataWrapper.server = server;
        DataWrapper.ALL.forEach((data) -> {
            if (data.shouldSave) data.load();
            if (data.shouldSync) data.sync();
        });
    }

    @Override
    public void onServerStop(MinecraftServer server){
        DataWrapper.server = null;
    }

    // TODO: we only have to save the data of the level being saved
    // TODO: players and global only overworld? does that work if you exit world from the nether? i think it says overworld stays loaded no matter what.
    @Override
    public void onLevelSave(LevelAccessor level){
        DataWrapper.ALL.forEach((data) -> {
            if (data.shouldSave && data.isDirty) data.save();
        });
    }

    @Override
    public void onPlayerLogin(Player player){
        if (player.level.isClientSide) return;

        DataWrapper.ALL.forEach((data) -> {
            if (data.shouldSync) data.sync();
        });
    }
}
