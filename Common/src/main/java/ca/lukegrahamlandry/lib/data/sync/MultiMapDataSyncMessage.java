package ca.lukegrahamlandry.lib.data.sync;

import ca.lukegrahamlandry.lib.data.DataWrapper;
import ca.lukegrahamlandry.lib.data.MapDataWrapper;
import ca.lukegrahamlandry.lib.network.ClientboundHandler;
import com.google.gson.JsonObject;

import java.util.Objects;

public class MultiMapDataSyncMessage implements ClientboundHandler {
    String value;
    String name;
    String dir;

    public MultiMapDataSyncMessage(MapDataWrapper<?, ?, ?> wrapper) {
        this.name = wrapper.getName();
        this.dir = wrapper.getSubDirectory();

        // encode here using ConfigWrapper#getGson instead of allowing the object to be encoded by the packet module's gson instance
        // this allows adding type adapters to your ConfigWrapper and still having syncing
        // TODO: since this is a Map<S, V> instead of a Map<S, GenericHolder<V>> the entries may not be subclasses of V since the type info will be lost in json conversion
        this.value = wrapper.getGson().toJson(wrapper.getMap());
    }

    public void handle() {
        for (DataWrapper<?> data : DataWrapper.ALL) {
            if (data instanceof MapDataWrapper<?, ?, ?> && Objects.equals(this.dir, data.getSubDirectory()) && data.getName().equals(this.name)) {
                JsonObject syncedValue = data.getGson().fromJson(this.value, JsonObject.class);
                ((MapDataWrapper<?, ?, ?>) data).loadFromMap(syncedValue);
                break;
            }
        }

        throw new RuntimeException("received data sync for unknown {name: " + this.name + ", dir: " + this.dir + "}");
    }
}
