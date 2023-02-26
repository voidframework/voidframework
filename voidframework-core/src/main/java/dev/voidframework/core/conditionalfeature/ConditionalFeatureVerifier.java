package dev.voidframework.core.conditionalfeature;

import com.typesafe.config.Config;
import dev.voidframework.core.conditionalfeature.condition.Condition;
import dev.voidframework.core.exception.ConditionalFeatureException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Checks whether a feature should be activated or not. Any class that
 * does not have the appropriate annotations will be considered active.
 *
 * @see ConditionalFeature
 * @see Condition
 * @since 1.5.0
 */
public final class ConditionalFeatureVerifier {

    private static final List<String> EXCLUDED_METHOD_LIST = List.of("annotationType", "hashCode", "toString");

    private final Config configuration;
    private final Map<Class<? extends Condition>, Condition> conditionCacheMap;

    /**
     * Build a new instance.
     *
     * @param configuration The application configuration
     * @since 1.5.0
     */
    public ConditionalFeatureVerifier(final Config configuration) {

        this.configuration = configuration;
        this.conditionCacheMap = new HashMap<>();
    }

    /**
     * Checks if the feature is disabled.
     *
     * @return {@code true} if the feature is disabled, otherwise, {@code false}
     * @since 1.5.0
     */
    public boolean isFeatureDisabled(final Class<?> classType) {

        for (final Annotation annotation : classType.getAnnotations()) {

            if (annotation instanceof ConditionalFeature conditionalFeature) {
                final Condition condition = this.conditionCacheMap.computeIfAbsent(conditionalFeature.value(), this::instantiateCondition);

                return !condition.isEnabled(this.configuration, classType, AnnotationMetadata.EMPTY);
            } else {
                final ConditionalFeature conditionalFeature = annotation.annotationType().getAnnotation(ConditionalFeature.class);
                if (conditionalFeature != null) {

                    final AnnotationMetadata annotationMetadata = this.buildAnnotationMetadata(annotation);
                    final Condition condition = this.conditionCacheMap.computeIfAbsent(conditionalFeature.value(), this::instantiateCondition);

                    return !condition.isEnabled(this.configuration, classType, annotationMetadata);
                }
            }
        }

        return false;
    }

    /**
     * Creates an instance of the given condition class.
     *
     * @param conditionClassType Condition class type
     * @return Newly created instance
     * @since 1.5.0
     */
    private Condition instantiateCondition(final Class<? extends Condition> conditionClassType) {

        try {
            final Constructor<? extends Condition> constructor = conditionClassType.getDeclaredConstructor();
            return constructor.newInstance();
        } catch (final InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException exception) {
            throw new ConditionalFeatureException.ConditionInitFailure(conditionClassType, exception);
        }
    }

    /**
     * Builds annotation metadata.
     *
     * @param annotation Annotation to use
     * @return Annotation metadata
     * @since 1.5.0
     */
    private AnnotationMetadata buildAnnotationMetadata(final Annotation annotation) {

        final Map<String, Object> dataMap = new HashMap<>();
        for (final Method method : annotation.getClass().getDeclaredMethods()) {
            if (method.getParameterCount() != 0 || EXCLUDED_METHOD_LIST.contains(method.getName())) {
                continue;
            }

            try {
                final Object value = method.invoke(annotation);
                dataMap.put(method.getName(), value);
            } catch (final IllegalAccessException | InvocationTargetException ignore) {
                // Nothing to do
            }
        }

        return new AnnotationMetadata(dataMap);
    }
}
