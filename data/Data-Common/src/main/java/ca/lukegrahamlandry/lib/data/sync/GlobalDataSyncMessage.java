package ca.lukegrahamlandry.lib.data.sync;

import ca.lukegrahamlandry.lib.base.GenericHolder;
import ca.lukegrahamlandry.lib.data.DataWrapper;
import ca.lukegrahamlandry.lib.data.GlobalDataWrapper;
import ca.lukegrahamlandry.lib.packets.ClientboundHandler;

import java.util.Objects;

public class GlobalDataSyncMessage implements ClientboundHandler {
    String value;
    String name;
    String dir;

    public GlobalDataSyncMessage(GlobalDataWrapper<?> wrapper) {
        this.name = wrapper.getName();
        this.dir = wrapper.getSubDirectory();

        // encode here using ConfigWrapper#getGson instead of allowing the object to be encoded by the packet module's gson instance
        // this allows adding type adapters to your ConfigWrapper and still having syncing
        this.value = wrapper.getGson().toJson(new GenericHolder<>(wrapper.get()));
    }

    public void handle() {
        for (DataWrapper<?> data : DataWrapper.ALL) {
            if (data instanceof GlobalDataWrapper && Objects.equals(this.dir, data.getSubDirectory()) && data.getName().equals(this.name)) {
                GenericHolder<?> syncedValue = data.getGson().fromJson(this.value, GenericHolder.class);
                ((GlobalDataWrapper<?>) data).set(syncedValue.value);
                break;
            }
        }

        throw new RuntimeException("received data sync for unknown {name: " + this.name + ", dir: " + this.dir + "}");
    }
}
