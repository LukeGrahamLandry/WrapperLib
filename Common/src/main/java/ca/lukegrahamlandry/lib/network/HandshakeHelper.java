/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.network;

import ca.lukegrahamlandry.lib.base.SafeClientHelper;
import ca.lukegrahamlandry.lib.base.event.IEventCallbacks;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

class HandshakeHelper implements IEventCallbacks {
    private static final HashMap<String, ModProtocol> ACTIVE_VERSIONS = new HashMap<>();
    public static final HashMap<String, Predicate<String>> CLIENT_VERSION_CHECKERS = new HashMap<>();
    public static final HashMap<String, Predicate<String>> SERVER_VERSION_CHECKERS = new HashMap<>();

    public static void add(@NotNull ModProtocol protocol) {
        ACTIVE_VERSIONS.put(protocol.modid, protocol);
    }

    private static void logProtocolVersions(){
        StringBuilder debug = new StringBuilder();
        for (ModProtocol protocol : ACTIVE_VERSIONS.values()){
            debug.append(protocol).append(" ");
        }
        if (!debug.isEmpty()) NetworkWrapper.LOGGER.info("Mod Protocol Versions: " + debug);
    }

    @Override
    public void onPlayerLoginServer(@NotNull Player player) {
        new HandshakeMessage(ACTIVE_VERSIONS.values()).sendToClient((ServerPlayer) player);
    }

    @Override
    public void onClientSetup() {
        logProtocolVersions();
    }

    @Override
    public void onServerStarting(@NotNull MinecraftServer server) {
        logProtocolVersions();
    }

    private static class HandshakeMessage implements ServerSideHandler, ClientSideHandler {
        public List<ModProtocol> mods;
        public HandshakeMessage(Collection<ModProtocol> mods){
            this.mods = new ArrayList<>(mods);
        }

        @Override
        public void handle() {
            boolean accepted = this.validate(CLIENT_VERSION_CHECKERS, (msg) -> SafeClientHelper.getMinecraft().player.connection.getConnection().disconnect(msg), "client");
            if (accepted){
                NetworkWrapper.LOGGER.info("client accepts server's mod protocol versions");
                new HandshakeMessage(ACTIVE_VERSIONS.values()).sendToServer();
            }
        }

        @Override
        public void handle(ServerPlayer player) {
            this.validate(SERVER_VERSION_CHECKERS, (msg) -> player.connection.disconnect(msg), "server");
        }

        private boolean validate(HashMap<String, Predicate<String>> sidedVersionCheckers, Consumer<Component> sendDisconnect, String side){
            for (ModProtocol protocol : mods){
                String otherSideVersion = protocol.version;

                boolean accepted;
                try {
                    accepted = sidedVersionCheckers.getOrDefault(protocol.modid, (s) -> true).test(otherSideVersion);
                } catch (Exception e){
                    accepted = false;
                }

                if (!accepted) {
                    sendDisconnect.accept(error(protocol, side));
                    return false;
                }
            }
            return true;
        }

        private static Component error(ModProtocol protocol, String side){
            String currentSideVersion = ACTIVE_VERSIONS.containsKey(protocol.modid) ? ACTIVE_VERSIONS.get(protocol.modid).version : "NONE";
            String otherSide = side.equals("server") ? "client" : "server";
            String v = protocol.modid + "'s active protocol versions are (" + side + ": " + currentSideVersion + ", " + otherSide + ": " + protocol.version + ")";
            NetworkWrapper.LOGGER.info(v);
            return Component.literal("Mod (" + protocol.modid + ") network protocol not accepted by " + side + ". Ensure client and server have same mod version. " + v);
        }
    }

    public static class ModProtocol {
        String modid;
        String version;
        public ModProtocol(String modid, String version){
            this.modid = modid;
            this.version = version;
        }

        @Override
        public String toString() {
            return "(" + this.modid + ", " + this.version + ")";
        }
    }
}
