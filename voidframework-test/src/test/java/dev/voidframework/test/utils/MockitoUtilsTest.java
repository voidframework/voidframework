package dev.voidframework.test.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class MockitoUtilsTest {

    @Test
    void constructor() throws NoSuchMethodException {

        // Act
        final Constructor<MockitoUtils> constructor = MockitoUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final InvocationTargetException exception = Assertions.assertThrows(InvocationTargetException.class, constructor::newInstance);

        // Assert
        Assertions.assertNotNull(exception.getCause());
        Assertions.assertEquals("This is a utility class and cannot be instantiated", exception.getCause().getMessage());
    }

    @Test
    void spyConsumer() {

        // Arrange
        final Consumer<String> stringConsumer = System.out::println;

        // Act
        final Consumer<String> stringConsumerSpy = MockitoUtils.spyConsumer(stringConsumer);
        stringConsumerSpy.accept("Pear");

        // Assert
        Mockito.verify(stringConsumerSpy, Mockito.times(1)).accept("Pear");
    }

    @Test
    void spyBiConsumer() {

        // Arrange
        final BiConsumer<String, Integer> stringIntegerConsumer = (s, i) -> System.out.println(s + "-" + i);

        // Act
        final BiConsumer<String, Integer> stringIntegerConsumerSpy = MockitoUtils.spyBiConsumer(stringIntegerConsumer);
        stringIntegerConsumerSpy.accept("Pear", 1337);

        // Assert
        Mockito.verify(stringIntegerConsumerSpy, Mockito.times(1)).accept("Pear", 1337);
    }

    @Test
    void spyFunction() {

        // Arrange
        final Function<String, Integer> stringToIntegerFunction = Integer::valueOf;

        // Act
        final Function<String, Integer> stringToIntegerFunctionSpy = MockitoUtils.spyFunction(stringToIntegerFunction);
        stringToIntegerFunctionSpy.apply("1337");

        // Assert
        Mockito.verify(stringToIntegerFunctionSpy, Mockito.times(1)).apply("1337");
    }

    @Test
    void spyBiFunction() {

        // Arrange
        final BiFunction<String, Integer, Integer> stringToIntegerFunction = Integer::valueOf;

        // Act
        final BiFunction<String, Integer, Integer> stringToIntegerFunctionSpy = MockitoUtils.spyBiFunction(stringToIntegerFunction);
        stringToIntegerFunctionSpy.apply("1337", 10);

        // Assert
        Mockito.verify(stringToIntegerFunctionSpy, Mockito.times(1)).apply("1337", 10);
    }

    @Test
    void spyPredicate() {

        // Arrange
        final Predicate<String> stringPredicate = (s) -> true;

        // Act
        final Predicate<String> stringPredicateSpy = MockitoUtils.spyPredicate(stringPredicate);
        stringPredicateSpy.test("Pear");

        // Assert
        Mockito.verify(stringPredicateSpy, Mockito.times(1)).test("Pear");
    }

    @Test
    void spyBiPredicate() {

        // Arrange
        final BiPredicate<String, String> stringStringPredicate = Objects::equals;

        // Act
        final BiPredicate<String, String> stringStringPredicateSpy = MockitoUtils.spyBiPredicate(stringStringPredicate);
        stringStringPredicateSpy.test("Pear", "Apple");

        // Assert
        Mockito.verify(stringStringPredicateSpy, Mockito.times(1)).test("Pear", "Apple");
    }

    @Test
    void spySupplier() {

        // Arrange
        final Supplier<String> stringSupplier = () -> "Pear";

        // Act
        final Supplier<String> stringSupplierSpy = MockitoUtils.spySupplier(stringSupplier);
        stringSupplierSpy.get();

        // Assert
        Mockito.verify(stringSupplierSpy, Mockito.times(1)).get();
    }
}
