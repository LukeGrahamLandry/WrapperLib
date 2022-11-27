package ca.lukegrahamlandry.lib.data.type;

import ca.lukegrahamlandry.lib.data.DataWrapper;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// TODO: something clever for only syncing tracked players
// TODO: something clever for only loading players that are actually logged in for big servers

/**
 * Data is saved per uuid not per player entity so data is maintained between deaths and dimension changes.
 */
public class PlayerDataWrapper<T> extends DataWrapper<T> {
    /**
     * Save in dir/name/uuid.etx {data} instead of dir/name.etx {uuid: data}
     */
    public PlayerDataWrapper<T> individualFiles(){
        return this;
    }

    ///// IMPL /////

    protected Map<UUID, T> data = new HashMap<>();
    public PlayerDataWrapper(Class<T> clazz) {
        super(clazz);
    }

    public T get(Player player){
        if (!this.loaded) {
            this.logger.error("cannot call DataWrapper get (a) before server startup (b) on client if unsynced");
            return null;
        }

        T value = data.get(player.getUUID());
        if (value == null){
            value = this.createDefaultInstance();
            data.put(player.getUUID(), value);
        }

        return value;
    }
}
