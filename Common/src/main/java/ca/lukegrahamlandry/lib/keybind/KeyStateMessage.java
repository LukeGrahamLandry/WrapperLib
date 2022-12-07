package ca.lukegrahamlandry.lib.keybind;

import ca.lukegrahamlandry.lib.network.ServerSideHandler;
import net.minecraft.server.level.ServerPlayer;

public class KeyStateMessage implements ServerSideHandler {
    String id;
    boolean pressed;
    public KeyStateMessage(KeybindWrapper key){
        this.id = key.mapping.getName();
        this.pressed = key.mapping.isDown();
    }

    @Override
    public void handle(ServerPlayer player) {
        KeybindWrapper wrapper = KeybindWrapper.ALL.get(id);
        if (wrapper == null){
            return;
        }

        wrapper.pressed.put(player.getUUID(), pressed);

        if (pressed) {
            wrapper.onPressAction.accept(player);
        } else {
            wrapper.onReleaseAction.accept(player);
        }
    }
}
