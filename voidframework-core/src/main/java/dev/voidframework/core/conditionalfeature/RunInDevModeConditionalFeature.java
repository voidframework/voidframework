package dev.voidframework.core.conditionalfeature;

import dev.voidframework.core.conditionalfeature.condition.RunInDevModeCondition;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the feature will only be loaded if the application is run in development
 * mode (see configuration key {@code voidframework.core.runInDevMode}").
 *
 * @since 1.7.0
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ConditionalFeature(RunInDevModeCondition.class)
public @interface RunInDevModeConditionalFeature {
}
