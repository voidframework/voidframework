package dev.voidframework.test.utils;

import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Mockito utility methods.
 *
 * @since 1.11.0
 */
public final class MockitoUtils {

    /**
     * Default constructor.
     *
     * @since 1.11.0
     */
    private MockitoUtils() {

        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Creates a spy of a real lambda function.
     *
     * @param lambdaClassType The class of the lambda function to spy
     * @param lambda          The lambda function to spy
     * @param <C>             The type of the lambda class type
     * @param <L>             The lambda type
     * @return A spy of the provided lambda function
     * @since 1.11.0
     */
    @SuppressWarnings("unchecked")
    public static <C, L extends C> L spyLambda(final Class<C> lambdaClassType, final L lambda) {

        return (L) Mockito.mock(lambdaClassType, AdditionalAnswers.delegatesTo(lambda));
    }

    /**
     * Creates a spy of a real consumer.
     *
     * @param consumer The consumer to spy
     * @param <C>      The consumer type
     * @return A spy of the provided consumer
     * @since 1.11.0
     */
    public static <C extends Consumer<?>> C spyConsumer(final C consumer) {

        return spyLambda(Consumer.class, consumer);
    }

    /**
     * Creates a spy of a real consumer that accept two parameters.
     *
     * @param biconsumer The consumer to spy
     * @param <C>        The consumer type
     * @return A spy of the provided consumer
     * @since 1.11.0
     */
    public static <C extends BiConsumer<?, ?>> C spyBiConsumer(final C biconsumer) {

        return spyLambda(BiConsumer.class, biconsumer);
    }

    /**
     * Creates a spy of a real function.
     *
     * @param function The function to spy
     * @param <F>      The function type
     * @return A spy of the provided function
     * @since 1.11.0
     */
    public static <F extends Function<?, ?>> F spyFunction(final F function) {

        return spyLambda(Function.class, function);
    }

    /**
     * Creates a spy of a real function that accept two parameters.
     *
     * @param bifunction The function to spy
     * @param <F>        The function type
     * @return A spy of the provided function
     * @since 1.11.0
     */
    public static <F extends BiFunction<?, ?, ?>> F spyBiFunction(final F bifunction) {

        return spyLambda(BiFunction.class, bifunction);
    }

    /**
     * Creates a spy of a real predicate.
     *
     * @param predicate The predicate to spy
     * @param <P>       The predicate type
     * @return A spy of the provided predicate
     * @since 1.11.0
     */
    public static <P extends Predicate<?>> P spyPredicate(final P predicate) {

        return spyLambda(Predicate.class, predicate);
    }

    /**
     * Creates a spy of a real predicate that accept two parameters.
     *
     * @param bipredicate The predicate to spy
     * @param <P>         The predicate type
     * @return A spy of the provided predicate
     * @since 1.11.0
     */
    public static <P extends BiPredicate<?, ?>> P spyBiPredicate(final P bipredicate) {

        return spyLambda(BiPredicate.class, bipredicate);
    }

    /**
     * Creates a spy of a real supplier.
     *
     * @param supplier The supplier to spy
     * @param <S>      The supplier type
     * @return A spy of the provided supplier
     * @since 1.11.0
     */
    public static <S extends Supplier<?>> S spySupplier(final S supplier) {

        return spyLambda(Supplier.class, supplier);
    }
}
