package ca.lukegrahamlandry.lib.examplemod;

import ca.lukegrahamlandry.lib.config.Comment;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ExampleConfig {
    @Comment("the amplifier of speed effect to give when an entity jumps")
    public int speedLevel = 2;
    @Comment("this is a number")
    public float something = 2.5F;
    @Comment("what should i say")
    public String hello = "world";

    @Comment("monsters to spawn")
    public Map<ResourceLocation, Integer> sizes = new HashMap<>();

    public ExampleConfig(){
        sizes.put(new ResourceLocation("minecraft:pig"), 10);
    }
}
