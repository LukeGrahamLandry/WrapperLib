package ca.lukegrahamlandry.examplemod.fabric;

import ca.lukegrahamlandry.examplemod.ExampleClientMain;
import ca.lukegrahamlandry.examplemod.ExampleEventHandlers;
import ca.lukegrahamlandry.lib.event.fabric.WrapperLibClientInitializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class ExampleClientInit implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        new WrapperLibClientInitializer().onInitializeClient();
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> ExampleEventHandlers.drawOverlay(matrixStack));
        ExampleClientMain.init();
    }
}
