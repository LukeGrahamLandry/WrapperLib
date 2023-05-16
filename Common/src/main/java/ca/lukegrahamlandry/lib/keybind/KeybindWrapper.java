/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.keybind;

import ca.lukegrahamlandry.lib.WrapperLibException;
import ca.lukegrahamlandry.lib.base.Available;
import ca.lukegrahamlandry.lib.helper.PlatformHelper;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class KeybindWrapper {
    /**
     * This can safely be called from common code (it will not crash dedicated servers).
     * @param defaultKey ex. GLFW.GLFW_KEY_A
     */
    public static KeybindWrapper of(String name, String category, int defaultKey){
        return new KeybindWrapper("key." + category + "." + name, defaultKey, "key.categories." + category);
    }

    /**
     * This can safely be called from common code (it will not crash dedicated servers).
     * The key will start off as unbound and the player must set it to something in the options menu before using.
     */
    public static KeybindWrapper of(String name, String category){
        return of(name, category, -1);
    }

    /**
     * Cause the state of your keybind to sync to the logical server.
     * This will cause the press, release, and hold actions to fire on the logical server as well.
     */
    public KeybindWrapper synced(){
        if (!Available.NETWORK.get()) WrapperLibException.maybeThrow("Called KeybindWrapper#synced but WrapperLib Network module is missing.");
        this.shouldSync = true;
        return this;
    }

    /**
     * @param action will be called when a player initially presses the key.
     */
    public KeybindWrapper onPress(Consumer<@NotNull Player> action){
        this.onPressAction = action;
        return this;
    }

    /**
     * @param action will be called when a player releases the key.
     */
    public KeybindWrapper onRelease(Consumer<@NotNull Player> action){
        this.onReleaseAction = action;
        return this;
    }

    /**
     * @param action will be called every tick while a player holds down the key.
     */
    public KeybindWrapper onHeldTick(Consumer<@NotNull Player> action){
        this.onHeldTickAction = action;
        return this;
    }

    // API

    public boolean isPressed(@Nullable Player player){
        if (player == null || !player.isAlive()) return false;
        return pressed.getOrDefault(player.getUUID(), false);
    }

    // IMPL

    static Map<String, KeybindWrapper> ALL = new HashMap<>();
    final String id;
    Consumer<Player> onPressAction = (p) -> {};
    Consumer<Player> onReleaseAction = (p) -> {};
    Consumer<Player> onHeldTickAction = (p) -> {};
    boolean shouldSync = false;
    Map<UUID, Boolean> pressed = new HashMap<>();
    KeyMapping mapping = null;
    public KeybindWrapper(@NotNull String nameTranslationId, int defaultKey, @NotNull String categoryTranslationId){
        this.id = nameTranslationId;
        ALL.put(this.id, this);

        if (!Available.PLATFORM_HELPER.get()) WrapperLibException.maybeThrow("Tried to create KeybindWrapper but WrapperLib PlatformHelper is missing.");
        if (PlatformHelper.isDedicatedServer()) return;

        this.mapping = new KeyMapping(nameTranslationId, defaultKey, categoryTranslationId);
        register(this.mapping);
    }

    /**
     * Adds a new key bind to the vanilla settings gui.
     * This will automatically be called when you create a new KeybindWrapper (but not on dedicated servers).
     * This may ONLY be called on the CLIENT.
     */
    @ExpectPlatform
    public static void register(@NotNull KeyMapping key){
        throw new AssertionError();
    }
}
