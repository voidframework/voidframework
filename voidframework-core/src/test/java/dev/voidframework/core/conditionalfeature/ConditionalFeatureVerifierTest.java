package dev.voidframework.core.conditionalfeature;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.core.conditionalfeature.condition.Condition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class ConditionalFeatureVerifierTest {

    static Stream<Arguments> isFeatureDisabledConfigurationNamedKeyValueArguments() {
        return Stream.of(
            Arguments.of(Named.of("disabled (empty cfg)", ConfigFactory.empty()), true),
            Arguments.of(Named.of("disabled (0)", ConfigFactory.parseString("feature.name=0")), true),
            Arguments.of(Named.of("disabled (false)", ConfigFactory.parseString("feature.name=false")), true),
            Arguments.of(Named.of("enabled (true)", ConfigFactory.parseString("feature.name=true")), false),
            Arguments.of(Named.of("enabled (yes)", ConfigFactory.parseString("feature.name=yes")), false),
            Arguments.of(Named.of("enabled (enabled)", ConfigFactory.parseString("feature.name=enabled")), false),
            Arguments.of(Named.of("enabled (1)", ConfigFactory.parseString("feature.name=1")), false));
    }

    @ParameterizedTest
    @MethodSource("isFeatureDisabledConfigurationNamedKeyValueArguments")
    void isFeatureDisabledFromConfiguration(final Config configuration, final boolean expectedResult) {

        // Arrange
        final ConditionalFeatureVerifier conditionalFeatureVerifier = new ConditionalFeatureVerifier(configuration);

        // Act
        final boolean isDisabled = conditionalFeatureVerifier.isFeatureDisabled(DummyController1.class);

        // Assert
        Assertions.assertEquals(expectedResult, isDisabled);
    }

    @Test
    void isFeatureDisabledFromCustomCondition() {

        // Arrange
        final Config configuration = ConfigFactory.empty();
        final ConditionalFeatureVerifier conditionalFeatureVerifier = new ConditionalFeatureVerifier(configuration);

        // Act
        final boolean isDisabled = conditionalFeatureVerifier.isFeatureDisabled(DummyController2.class);

        // Assert
        Assertions.assertFalse(isDisabled);
    }

    @Test
    void isFeatureDisabledNoAnnotation() {

        // Arrange
        final Config configuration = ConfigFactory.empty();
        final ConditionalFeatureVerifier conditionalFeatureVerifier = new ConditionalFeatureVerifier(configuration);

        // Act
        final boolean isDisabled = conditionalFeatureVerifier.isFeatureDisabled(DummyController3.class);

        // Assert
        Assertions.assertFalse(isDisabled);
    }

    @Test
    void isFeatureDisabledCannotCreateConditionInstance() {

        // Arrange
        final Config configuration = ConfigFactory.empty();
        final ConditionalFeatureVerifier conditionalFeatureVerifier = new ConditionalFeatureVerifier(configuration);

        // Act
        final RuntimeException exception = Assertions.assertThrows(
            RuntimeException.class,
            () -> conditionalFeatureVerifier.isFeatureDisabled(DummyController4.class));

        // Assert
        Assertions.assertTrue(exception.getMessage().contains("ConditionalFeatureVerifier cannot access a member of class"));
    }

    /**
     * Dummy condition.
     */
    public static final class DummyCondition implements Condition {

        @Override
        public boolean isEnabled(final Config configuration,
                                 final Class<?> annotatedClassType,
                                 final AnnotationMetadata annotationMetadata) {

            return true;
        }
    }

    /**
     * Dummy condition.
     */
    public static final class DummyConditionInvalidConstructor implements Condition {

        /**
         * Build a new instance.
         */
        private DummyConditionInvalidConstructor() {
        }

        @Override
        public boolean isEnabled(final Config configuration,
                                 final Class<?> annotatedClassType,
                                 final AnnotationMetadata annotationMetadata) {

            return true;
        }
    }

    /**
     * Dummy controller 1.
     */
    @ConfigurationConditionalFeature("feature.name")
    private static final class DummyController1 {
    }

    /**
     * Dummy controller 2.
     */
    @ConditionalFeature(DummyCondition.class)
    private static final class DummyController2 {
    }

    /**
     * Dummy controller 3.
     */
    private static final class DummyController3 {
    }

    /**
     * Dummy controller 2.
     */
    @ConditionalFeature(DummyConditionInvalidConstructor.class)
    private static final class DummyController4 {
    }
}
