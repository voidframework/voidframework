package dev.voidframework.core.lang;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents a value of one or two possible types (a disjoint union).
 *
 * @param <L> Type of "Left" value
 * @param <R> Type of "Right" value
 */
public class Either<L, R> {

    private final L left;
    private final R right;

    /**
     * Build a new instance.
     *
     * @param left  The "Left" value
     * @param right The "Right" value
     */
    private Either(final L left, final R right) {

        this.left = left;
        this.right = right;
    }

    /**
     * Creates a {@code Either} with left value set.
     *
     * @param left The "Left" value
     * @param <L>  Type of "Left" value
     * @param <R>  Type of "Right" value
     * @return The newly created Either
     */
    public static <L, R> Either<L, R> ofLeft(final L left) {

        return new Either<>(left, null);
    }

    /**
     * Creates a {@code Either} with right value set.
     *
     * @param right The "Right" value
     * @param <L>   Type of "Left" value
     * @param <R>   Type of "Right" value
     * @return The newly created Either
     */
    public static <L, R> Either<L, R> ofRight(final R right) {

        return new Either<>(null, right);
    }

    /**
     * Returns the "Left" value.
     *
     * @return The "Left" value
     */
    public L getLeft() {

        return this.left;
    }

    /**
     * Returns the "Right" value.
     *
     * @return The "Right" value
     */
    public R getRight() {

        return this.right;
    }

    /**
     * Checks if the "Left" value is set.
     *
     * @return {@code true} if the "Left" value is set, otherwise, {@code false}
     */
    public boolean hasLeft() {

        return this.left != null;
    }

    /**
     * Checks if the "Right" value is set.
     *
     * @return {@code true} if the "Right" value is set, otherwise, {@code false}
     */
    public boolean hasRight() {

        return this.right != null;
    }

    /**
     * Applies the right consumer.
     *
     * @param leftConsumer  The "Left" consumer
     * @param rightConsumer The "Right" consumer
     */
    public void match(final Consumer<L> leftConsumer,
                      final Consumer<R> rightConsumer) {

        if (this.left != null && leftConsumer != null) {
            leftConsumer.accept(this.left);
        } else if (this.right != null && rightConsumer != null) {
            rightConsumer.accept(this.right);
        } else {
            throw new IllegalArgumentException((this.left != null ? "Left" : "Right") + " consumer is required, but was null");
        }
    }

    /**
     * Applies the right function.
     *
     * @param leftFunction  The "Left" function
     * @param rightFunction The "Right" function
     * @param <U> The returned value type
     * @return The result of the applied function
     */
    public <U> U match(final Function<L, U> leftFunction,
                       final Function<R, U> rightFunction) {

        if (this.left != null && leftFunction != null) {
            return leftFunction.apply(this.left);
        }

        if (this.right != null && rightFunction != null) {
            return rightFunction.apply(this.right);
        }

        throw new IllegalArgumentException((this.left != null ? "Left" : "Right") + " function is required, but was null");
    }
}
