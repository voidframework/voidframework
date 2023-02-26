package dev.voidframework.core.conditionalfeature;

import dev.voidframework.core.conditionalfeature.condition.ConfigurationCondition;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the feature will only be loaded if, at least, one condition is met. The
 * values are retrieved from the configuration, properties or environment variables.
 *
 * @since 1.5.0
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ConditionalFeature(ConfigurationCondition.class)
public @interface ConfigurationConditionalFeature {

    /**
     * Defines the name of the values to be read from the
     * configuration, properties or environment variables.
     *
     * @return Name of the values to be read
     * @since 1.5.0
     */
    String[] value();

    /**
     * Defines all the expected values to enable the feature. Only
     * one of these is needed to activate the feature.
     *
     * @return Expected value to enable the feature
     * @since 1.5.0
     */
    String[] expectedValue() default {"true", "enabled", "yes", "1"};
}
