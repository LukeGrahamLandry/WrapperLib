package ca.lukegrahamlandry.lib.data.type;

import ca.lukegrahamlandry.lib.data.DataWrapper;
import net.minecraft.world.level.Level;

public class LevelDataWrapper<T> extends DataWrapper<T> {
    public LevelDataWrapper(Class<T> clazz) {
        super(clazz);
    }

    public T get(Level level){
        if (!this.loaded) {
            this.logger.error("cannot call DataWrapper get (a) before server startup (b) on client if unsynced");
            return null;
        }

        return null;
    }
}
