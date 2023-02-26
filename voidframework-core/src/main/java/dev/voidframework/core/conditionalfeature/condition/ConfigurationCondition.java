package dev.voidframework.core.conditionalfeature.condition;

import com.typesafe.config.Config;
import dev.voidframework.core.conditionalfeature.AnnotationMetadata;

import java.util.Arrays;
import java.util.List;

/**
 * Indicates that the feature will only be loaded if the condition is met. The value
 * is retrieved from the configuration, properties or environment variables.
 *
 * @since 1.5.0
 */
public class ConfigurationCondition implements Condition {

    @Override
    public boolean isEnabled(final Config configuration,
                             final Class<?> annotatedClassType,
                             final AnnotationMetadata annotationMetadata) {

        final List<String> expectedValueList = Arrays.asList(annotationMetadata.getStringArray("expectedValue"));
        final String[] keyArray = annotationMetadata.getStringArray("value");

        for (final String key : keyArray) {

            final String currentValue;
            if (configuration.hasPath(key)) {
                currentValue = configuration.getString(key);
            } else {
                currentValue = System.getenv(key);
            }

            if (expectedValueList.contains(currentValue)) {
                return true;
            }
        }

        return false;
    }
}
