package dev.voidframework.core.conditionalfeature;

import dev.voidframework.core.conditionalfeature.condition.Condition;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the feature will only be loaded if the condition is met.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConditionalFeature {

    /**
     * Condition to use to determine if the feature must be enabled.
     *
     * @return Condition class type
     */
    Class<? extends Condition> value();
}
