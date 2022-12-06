package ca.lukegrahamlandry.lib.registry;

import ca.lukegrahamlandry.lib.base.Available;
import ca.lukegrahamlandry.lib.helper.PlatformHelper;
import ca.lukegrahamlandry.lib.helper.EntityHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @param <T> the type of object that we are
 */
public class RegistryThing<T> implements Supplier<T> {
    private static Logger LOGGER = LoggerFactory.getLogger(RegistryThing.class);
    public final Registry<?> registry;
    public final ResourceLocation rl;

    RegistryThing(Registry<?> registry, ResourceLocation rl){
        this.registry = registry;
        this.rl = rl;
    }

    @Override
    public T get() {
        return (T) this.registry.get(rl);
    }

    public ResourceLocation getId() {
        return this.rl;
    }

    // HELPERS

    /**
     * Creates a BlockItem for your Block.
     */
    public RegistryThing<T> withItem(){
        return this.withItem(() -> new BlockItem((Block) this.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));
    }

    /**
     * Creates a BlockItem for your Block.
     */
    public RegistryThing<T> withItem(Function<Block, BlockItem> constructor){
        return this.withItem(() -> constructor.apply((Block) this.get()));
    }

    /**
     * Creates a BlockItem for your Block.
     */
    public RegistryThing<T> withItem(Supplier<BlockItem> constructor){
        if (this.registry != Registry.BLOCK){
            LOGGER.error("Cannot call RegistryThing#withItem for " + this.rl + " (" + this.registry.key().location().getPath() + ", should be block)");
            return this;
        }
        RegistryWrapper.register(Registry.ITEM, this.rl, constructor);
        return this;
    }

    /**
     * Binds attributes to your EntityType. May only be called if the entity extends LivingEntity.
     */
    public RegistryThing<T> withAttributes(Supplier<AttributeSupplier.Builder> builder){
        if (this.registry != Registry.ENTITY_TYPE){
            LOGGER.error("Cannot call RegistryThing#withAttributes for " + this.rl + " (" + this.registry.key().location().getPath() + ", should be entity type)");
            return this;
        }
        if (!Available.ENTITY_HELPER.get()) throw new RuntimeException("Called RegistryThing#withAttributes but WrapperLib EntityHelper is missing.");

        EntityHelper.attributes(() -> (EntityType<? extends LivingEntity>) this.get(), builder);
        return this;
    }

    // https://www.youtube.com/watch?v=tXCuV_9naVI
    /**
     * Binds a renderer to your EntityType.
     * This can safely be called on the dedicated server because it checks before actually calling your supplier (and thus class loading the renderer).
     * @param provider {@code Supplier<EntityRendererProvider<O>>}, a supplier for your EntityRenderer constructor
     * @param <E> the type of entity we are
     */
    public <E extends Entity> RegistryThing<T> withRenderer(Supplier<Function<EntityRendererProvider.Context, EntityRenderer<E>>> provider){
        if (this.registry != Registry.ENTITY_TYPE){
            LOGGER.error("Cannot call RegistryThing#withRenderer for " + this.rl + " (" + this.registry.key().location().getPath() + ", should be entity type)");
            return this;
        }
        if (!Available.ENTITY_HELPER.get()) throw new RuntimeException("Called RegistryThing#withRenderer but WrapperLib EntityHelper is missing.");

        if (PlatformHelper.isDedicatedServer()) return this;
        EntityHelper.renderer(() -> (EntityType<? extends E>) this.get(), (ctx) -> provider.get().apply(ctx));
        return this;
    }

    /**
     * Creates a SpawnEggItem for your EntityType. May only be called if the entity extends Mob.
     */
    public RegistryThing<T> withSpawnEgg(int colourA, int colourB){
        return withSpawnEgg(colourA, colourB, new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    }

    /**
     * Creates a SpawnEggItem for your EntityType. May only be called if the entity extends Mob.
     */
    public RegistryThing<T> withSpawnEgg(int colourA, int colourB, Item.Properties props){
        if (this.registry != Registry.ENTITY_TYPE){
            LOGGER.error("Cannot call RegistryThing#withSpawnEgg for " + this.rl + " (" + this.registry.key().location().getPath() + ", should be entity type)");
            return this;
        }
        if (!Available.ENTITY_HELPER.get()) throw new RuntimeException("Called RegistryThing#withSpawnEgg but WrapperLib EntityHelper is missing.");

        Supplier<Item> egg = () -> EntityHelper.getSpawnEggConstructor().create(() -> (EntityType<? extends Mob>) this.get(), colourA, colourB, props);
        RegistryWrapper.register(Registry.ITEM, new ResourceLocation(this.rl.getNamespace(), this.rl.getPath() + "_spawn_egg"), egg);
        return this;
    }
}
