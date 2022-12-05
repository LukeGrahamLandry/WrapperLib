package ca.lukegrahamlandry.lib.registry;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 * @param <A> EntityType<? extends E>
 * @param <B> EntityRendererProvider<E>
 *         E  ? extends Entity
 */
public interface SideSafeRenderProvider<A, B> extends Supplier<Function<A, B>> {
}

