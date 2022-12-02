/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.network;

import ca.lukegrahamlandry.lib.base.event.IEventCallbacks;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.*;
import java.util.function.Predicate;

public class HandshakeHelper implements IEventCallbacks {
    private static final HashMap<String, ModProtocol> ACTIVE_VERSIONS = new HashMap<>();
    public static final HashMap<String, Predicate<String>> CLIENT_VERSION_CHECKERS = new HashMap<>();
    public static final HashMap<String, Predicate<String>> SERVER_VERSION_CHECKERS = new HashMap<>();

    public static void add(ModProtocol protocol) {
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
    public void onPlayerLoginServer(Player player) {
        NetworkWrapper.sendToClient((ServerPlayer) player, new HandshakeMessage(ACTIVE_VERSIONS.values()));
    }

    @Override
    public void onClientSetup() {
        logProtocolVersions();
    }

    @Override
    public void onServerStart(MinecraftServer server) {
        logProtocolVersions();
    }

    private static class HandshakeMessage implements ServerSideHandler, ClientSideHandler {
        public List<ModProtocol> mods;
        public HandshakeMessage(Collection<ModProtocol> mods){
            this.mods = new ArrayList<>(mods);
        }

        @Override
        public void handle() {
            for (ModProtocol protocol : mods){
                String versionOnServer = protocol.version;
                boolean accepted = CLIENT_VERSION_CHECKERS.getOrDefault(protocol.modid, (s) -> true).test(versionOnServer);
                if (!accepted) {
                    Minecraft.getInstance().player.connection.getConnection().disconnect(error(protocol, "client"));
                    return;
                }
            }

            NetworkWrapper.LOGGER.info("client accepts server's mod protocol versions");
            NetworkWrapper.sendToServer(new HandshakeMessage(ACTIVE_VERSIONS.values()));
        }

        @Override
        public void handle(ServerPlayer player) {
            for (ModProtocol protocol : mods){
                String versionOnClient = protocol.version;
                boolean accepted = SERVER_VERSION_CHECKERS.getOrDefault(protocol.modid, (s) -> true).test(versionOnClient);
                if (!accepted) {
                    player.connection.disconnect(error(protocol, "server"));
                }
            }
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
