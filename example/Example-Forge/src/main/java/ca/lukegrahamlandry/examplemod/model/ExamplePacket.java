package ca.lukegrahamlandry.examplemod.model;

import ca.lukegrahamlandry.lib.network.ClientSideHandler;
import net.minecraft.client.Minecraft;

public class ExamplePacket implements ClientSideHandler {
    private final int y;

    public ExamplePacket(int y){
        this.y = y;
    }

    @Override
    public void handle() {
        Minecraft.getInstance().player.setDeltaMovement(0, this.y, 0);
    }
}
