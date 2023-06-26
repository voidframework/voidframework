package dev.voidframework.restclient.annotation;

import dev.voidframework.core.proxyable.Proxyable;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that annotated interface define a REST client.
 *
 * @since 1.9.0
 */
@Proxyable
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RestClient {

    /**
     * The REST Client unique identifier.
     * This identifier is used to retrieve REST endpoint configuration.
     *
     * @return The REST Client unique identifier
     * @since 1.9.0
     */
    String value();
}
