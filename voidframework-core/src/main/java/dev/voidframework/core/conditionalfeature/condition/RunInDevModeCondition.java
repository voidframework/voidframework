package dev.voidframework.core.conditionalfeature.condition;

import com.typesafe.config.Config;
import dev.voidframework.core.conditionalfeature.AnnotationMetadata;

/**
 * Indicates that the feature will only be loaded if the application is run in development
 * mode (see configuration key {@code voidframework.core.runInDevMode}").
 *
 * @since 1.7.0
 */
public class RunInDevModeCondition implements Condition {

    @Override
    public boolean isEnabled(final Config configuration,
                             final Class<?> annotatedClassType,
                             final AnnotationMetadata annotationMetadata) {

        return configuration.getBoolean("voidframework.core.runInDevMode");
    }
}
