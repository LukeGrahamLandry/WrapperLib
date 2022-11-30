package ca.lukegrahamlandry.lib.data.impl.map;

import net.minecraft.world.entity.player.Player;

import java.util.UUID;

// TODO: something clever for only syncing tracked players
// TODO: something clever for only loading players that are actually logged in for big servers

/**
 * Data is saved per uuid not per player entity so data is maintained between deaths and dimension changes.
 */
public class PlayerDataWrapper<T> extends MapDataWrapper<Player, UUID, T> {
    ///// IMPL /////

    public PlayerDataWrapper(Class<T> clazz) {
        super(UUID.class, clazz);
    }

    @Override
    public UUID keyToId(Player key) {
        return key.getUUID();
    }

    @Override
    public UUID stringToId(String id) {
        return UUID.fromString(id);
    }
}
