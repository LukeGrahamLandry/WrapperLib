package ca.lukegrahamlandry.lib.registry;

import ca.lukegrahamlandry.lib.base.Available;
import ca.lukegrahamlandry.lib.helper.PlatformHelper;
import ca.lukegrahamlandry.lib.helper.EntityHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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
 *
 * @param <T> the type of the Registry we are registered in
 * @param <O> the type of object that we are, which is always a subclass of T
 */
public class RegistryThing<T, O> implements Supplier<O> {
    private static Logger LOGGER = LoggerFactory.getLogger(RegistryThing.class.getPackageName());
    public final Registry<T> registry;
    public final ResourceLocation rl;

    RegistryThing(Registry<T> registry, ResourceLocation rl){
        this.registry = registry;
        this.rl = rl;
    }

    @Override
    public O get() {
        return (O) this.registry.get(rl);
    }

    public ResourceLocation getId() {
        return this.rl;
    }

    public Holder<T> holder(){
        return this.registry.getHolderOrThrow(ResourceKey.create(this.registry.key(), this.rl));
    }

    // HELPERS

    /**
     * Creates a BlockItem for your Block.
     */
    public RegistryThing<T, O> withItem(){
        return this.withItem(() -> new BlockItem((Block) this.get(), new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
    }

    /**
     * Creates a BlockItem for your Block.
     */
    public RegistryThing<T, O> withItem(Function<Block, BlockItem> constructor){
        return this.withItem(() -> constructor.apply((Block) this.get()));
    }

    /**
     * Creates a BlockItem for your Block.
     */
    public RegistryThing<T, O> withItem(Supplier<BlockItem> constructor){
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
    public RegistryThing<T, O> withAttributes(Supplier<AttributeSupplier.Builder> builder){
        if (this.registry != Registry.ENTITY_TYPE){
            LOGGER.error("Cannot call RegistryThing#withAttributes for " + this.rl + " (" + this.registry.key().location().getPath() + ", should be entity type)");
            return this;
        }
        if (!Available.ENTITY_HELPER.get()) throw new RuntimeException("Called RegistryThing#withAttributes but WrapperLib EntityHelper module is missing.");

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
    public <E extends Entity> RegistryThing<T, O> withRenderer(Supplier<Function<EntityRendererProvider.Context, EntityRenderer<E>>> provider){
        if (this.registry != Registry.ENTITY_TYPE){
            LOGGER.error("Cannot call RegistryThing#withRenderer for " + this.rl + " (" + this.registry.key().location().getPath() + ", should be entity type)");
            return this;
        }
        if (!Available.ENTITY_HELPER.get()) throw new RuntimeException("Called RegistryThing#withAttributes but WrapperLib EntityHelper module is missing.");

        if (PlatformHelper.isDedicatedServer()) return this;
        EntityHelper.renderer(() -> (EntityType<? extends E>) this.get(), (ctx) -> provider.get().apply(ctx));
        return this;
    }
}
