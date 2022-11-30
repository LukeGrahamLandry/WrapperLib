package ca.lukegrahamlandry.lib.registry;

import net.minecraft.core.Registry;

public class RegistryWrapper<T> {
    public static <T> RegistryWrapper<T> of(Registry<T> vanillaRegistry, String modid){
        return new RegistryWrapper<>(vanillaRegistry, modid);
    }

    private final Registry<T> registry;
    private final String modid;
    public RegistryWrapper(Registry<T> vanillaRegistry, String modid) {
        this.registry = vanillaRegistry;
        this.modid = modid;
    }

    @ExpectPlatform
    public void init(){

    }

}
