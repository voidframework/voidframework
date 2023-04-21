package dev.voidframework.core.proxyable;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that an annotated interface can be proxy-ed. Such interfaces are considered as
 * candidates for auto-detection during the classpath scanning but are not automatically
 * bind/registered into Guice.
 *
 * @since 1.7.0
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Proxyable {
}
