package dev.voidframework.core.lang;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

@TestMethodOrder(MethodOrderer.MethodName.class)
public final class EitherTest {

    @Test
    void ofLeft() {

        // Act
        final Either<String, Integer> either = Either.ofLeft("Hello World!");

        // Assert
        Assertions.assertTrue(either.hasLeft());
        Assertions.assertFalse(either.hasRight());

        Assertions.assertEquals("Hello World!", either.getLeft());
        Assertions.assertNull(either.getRight());
    }

    @Test
    void ofRight() {

        // Act
        final Either<String, Integer> either = Either.ofRight(1773);

        // Assert
        Assertions.assertFalse(either.hasLeft());
        Assertions.assertTrue(either.hasRight());

        Assertions.assertNull(either.getLeft());
        Assertions.assertEquals(Integer.valueOf(1773), either.getRight());
    }

    @Test
    void matchLeftWithoutRetValue() {

        // Arrange
        final Either<String, Integer> either = Either.ofLeft("Hello World!");
        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        // Act
        either.match(left -> atomicBoolean.set(Objects.equals(left, "Hello World!")), right -> {
        });

        // Assert
        Assertions.assertTrue(atomicBoolean.get());
    }

    @Test
    void matchLeftWithRetValue() {

        // Arrange
        final Either<String, Integer> either = Either.ofLeft("Hello World!");

        // Act
        final Object object = either.match(left -> left, right -> right);

        // Assert
        Assertions.assertNotNull(object);
        Assertions.assertEquals("Hello World!", object);
    }

    @Test
    void matchRightWithoutRetValue() {

        // Arrange
        final Either<String, Integer> either = Either.ofRight(1773);
        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        // Act
        either.match(left -> {
        }, right -> atomicBoolean.set(right == 1773));

        // Assert
        Assertions.assertTrue(atomicBoolean.get());
    }

    @Test
    void matchRightWithRetValue() {

        // Arrange
        final Either<String, Integer> either = Either.ofRight(1773);

        // Act
        final Object object = either.match(left -> left, right -> right);

        // Assert
        Assertions.assertNotNull(object);
        Assertions.assertEquals(1773, object);
    }

    @Test
    void matchNullConsumer() {

        // Arrange
        final Either<String, Integer> either = Either.ofRight(1773);

        // Act & Assert
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> either.match(null, (Consumer<Integer>) null));
    }

    @Test
    void matchNullFunction() {

        // Arrange
        final Either<String, Integer> either = Either.ofRight(1773);

        // Act & Assert
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> either.match(null, (Function<Integer, String>) null));
    }
}
