package ca.lukegrahamlandry.lib.config;

import ca.lukegrahamlandry.lib.packets.ClientboundHandler;
import ca.lukegrahamlandry.lib.packets.GenericHolder;
import ca.lukegrahamlandry.lib.packets.PacketWrapper;

// TODO: check against subDirectory
public class ConfigSyncMessage implements ClientboundHandler {
    GenericHolder<?> value;
    String name;

    public ConfigSyncMessage(ConfigWrapper<?> wrapper){
        this.value = new GenericHolder<>(wrapper.get());
        this.name = wrapper.name;
    }

    public void handle(){
        for (ConfigWrapper<?> config : ConfigWrapper.ALL){
            if (config.name.equals(this.name) && config.side == ConfigWrapper.Side.SYNCED) {
                config.set(this.value.get());
                break;
            }
        }

        throw new RuntimeException("received config sync for unknown name: " + this.name);
    }
}
