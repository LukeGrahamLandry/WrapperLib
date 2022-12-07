package ca.lukegrahamlandry.lib.keybind;

import ca.lukegrahamlandry.lib.base.Available;
import ca.lukegrahamlandry.lib.helper.PlatformHelper;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class KeybindWrapper {
    /**
     * @param defaultKey ex. GLFW.GLFW_KEY_A
     */
    public static KeybindWrapper of(String name, String category, int defaultKey){
        return new KeybindWrapper("key." + category + "." + name, defaultKey, "key.categories." + category);
    }

    public static KeybindWrapper of(String name, String category){
        return of(name, category, -1);
    }

    public KeybindWrapper synced(){
        if (!Available.NETWORK.get()) throw new RuntimeException("Called KeybindWrapper#synced but WrapperLib Network module is missing.");
        this.shouldSync = true;
        return this;
    }

    public KeybindWrapper onPress(Consumer<Player> action){
        this.onPressAction = action;
        return this;
    }

    // does this fire if they die / log out?
    public KeybindWrapper onRelease(Consumer<Player> action){
        this.onReleaseAction = action;
        return this;
    }

    public KeybindWrapper onHeldTick(Consumer<Player> action){
        this.onHeldTickAction = action;
        return this;
    }

    // API

    public boolean isPressed(Player player){
        if (player == null || !player.isAlive()) return false;
        return pressed.getOrDefault(player.getUUID(), false);
    }

    // IMPL

    public static Map<String, KeybindWrapper> ALL = new HashMap<>();
    final String id;
    Consumer<Player> onPressAction = (p) -> {};
    Consumer<Player> onReleaseAction = (p) -> {};
    Consumer<Player> onHeldTickAction = (p) -> {};
    boolean shouldSync = false;
    Map<UUID, Boolean> pressed = new HashMap<>();
    KeyMapping mapping = null;
    public KeybindWrapper(String nameTranslationId, int defaultKey, String categoryTranslationId){
        this.id = nameTranslationId;
        ALL.put(this.id, this);
        if (!Available.PLATFORM_HELPER.get()) throw new RuntimeException("Tried to create KeybindWrapper but WrapperLib PlatformHelper is missing.");
        if (PlatformHelper.isDedicatedServer()) return;

        this.mapping = new KeyMapping(nameTranslationId, defaultKey, categoryTranslationId);
        register(this.mapping);
    }

    /**
     * This may ONLY be called on the CLIENT.
     */
    @ExpectPlatform
    public static void register(KeyMapping key){
        throw new AssertionError();
    }
}
