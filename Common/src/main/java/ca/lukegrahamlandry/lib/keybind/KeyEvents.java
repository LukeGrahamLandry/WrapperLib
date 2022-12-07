package ca.lukegrahamlandry.lib.keybind;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class KeyEvents {
    public static void clientTick(){
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        for (KeybindWrapper key : KeybindWrapper.ALL.values()){
            boolean wasDown = key.isPressed(player);
            if (key.mapping.isDown()){
                if (!wasDown){
                    key.pressed.put(player.getUUID(), true);
                    key.onPressAction.accept(player);
                    if (key.shouldSync) new KeyStateMessage(key).sendToServer();
                }
                key.onHeldTickAction.accept(player);
            } else if (wasDown) {
                key.pressed.put(player.getUUID(), false);
                key.onReleaseAction.accept(player);
                if (key.shouldSync) new KeyStateMessage(key).sendToServer();
            }
        }
    }

    public static void serverTick(ServerPlayer player){
        for (KeybindWrapper key : KeybindWrapper.ALL.values()){
            if (key.isPressed(player)){
                key.onHeldTickAction.accept(player);
            }
        }
    }
}
