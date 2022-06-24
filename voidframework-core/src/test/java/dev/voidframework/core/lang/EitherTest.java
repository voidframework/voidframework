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
    public void ofLeft() {
        final Either<String, Integer> either = Either.ofLeft("Hello World!");

        Assertions.assertTrue(either.hasLeft());
        Assertions.assertFalse(either.hasRight());

        Assertions.assertEquals("Hello World!", either.getLeft());
        Assertions.assertNull(either.getRight());
    }

    @Test
    public void ofRight() {
        final Either<String, Integer> either = Either.ofRight(1773);

        Assertions.assertFalse(either.hasLeft());
        Assertions.assertTrue(either.hasRight());

        Assertions.assertNull(either.getLeft());
        Assertions.assertEquals(Integer.valueOf(1773), either.getRight());
    }

    @Test
    public void matchLeft() {
        final Either<String, Integer> either = Either.ofLeft("Hello World!");
        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        either.match(left -> atomicBoolean.set(Objects.equals(left, "Hello World!")), right -> {
        });
        Assertions.assertTrue(atomicBoolean.get());

        final Object object = either.match(left -> left, right -> right);
        Assertions.assertNotNull(object);
        Assertions.assertEquals("Hello World!", object);
    }

    @Test
    public void matchRight() {
        final Either<String, Integer> either = Either.ofRight(1773);
        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        either.match(left -> {
        }, right -> atomicBoolean.set(right == 1773));
        Assertions.assertTrue(atomicBoolean.get());

        final Object object = either.match(left -> left, right -> right);
        Assertions.assertNotNull(object);
        Assertions.assertEquals(1773, object);
    }

    @Test
    public void matchNull() {
        final Either<String, Integer> either = Either.ofRight(1773);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            either.match(null, (Consumer<Integer>) null);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            either.match(null, (Function<Integer, String>) null);
        });
    }
}
