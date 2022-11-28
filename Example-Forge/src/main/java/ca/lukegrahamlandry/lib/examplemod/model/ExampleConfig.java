package ca.lukegrahamlandry.lib.examplemod.model;

import ca.lukegrahamlandry.lib.config.Comment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.checkerframework.checker.units.qual.C;

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

    @Comment("this is an nbt tag that gets serialized correctly")
    public CompoundTag tag = new CompoundTag();

    @Comment("this is an enchanted sword to give someone")
    public ItemStack sword = new ItemStack(Items.DIAMOND_SWORD, 1);

    public ExampleConfig(){
        sizes.put(new ResourceLocation("minecraft:pig"), 10);

        tag.putString("hello", "world");
        tag.putInt("number", 5);
        CompoundTag temp = new CompoundTag();
        temp.putIntArray("test", new int[]{1, 2, 3, 4});
        tag.put("data", temp);

        Map<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantments.FIRE_ASPECT, 2);
        EnchantmentHelper.setEnchantments(enchants, sword);
    }
}
