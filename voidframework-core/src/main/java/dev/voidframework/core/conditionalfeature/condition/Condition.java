package dev.voidframework.core.conditionalfeature.condition;

import com.typesafe.config.Config;
import dev.voidframework.core.conditionalfeature.AnnotationMetadata;

/**
 * A single condition to determine if feature must be enabled or not.
 */
public interface Condition {

    /**
     * Checks if the feature must be enabled or not.
     *
     * @param configuration      The application configuration
     * @param annotatedClassType Class type of the annotated class
     * @param annotationMetadata Annotation metadata
     * @return {@code true} if feature must be enabled, otherwise, {@code false}
     */
    boolean isEnabled(final Config configuration,
                      final Class<?> annotatedClassType,
                      final AnnotationMetadata annotationMetadata);
}
