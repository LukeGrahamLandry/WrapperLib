package ca.lukegrahamlandry.lib.data.impl;

import ca.lukegrahamlandry.lib.data.sync.TileFileDataMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Map;

public class TileFileDataWrapper<V> extends LevelDataWrapper<Map<BlockPos, V>> {
    public TileFileDataWrapper(Class<Map<BlockPos, V>> clazz) {
        super(clazz);
    }

    public V get(BlockEntity key) {
        if (!key.hasLevel()) return null;
        return get(key.getLevel()).get(key.getBlockPos());
    }

    public void setDirty(BlockEntity key) {
        this.isDirty = true;
        if (!key.hasLevel()) return;
        if (this.shouldSync) new TileFileDataMessage(this, key).sendToAllClients();
    }

    public void clear(BlockEntity key) {
        if (!key.hasLevel()) return;
        get(key.getLevel()).remove(key.getBlockPos());
    }

    public void set(ResourceLocation dim, BlockPos pos, Object value) {
        getById(dim).put(pos, (V) value);
    }
}
