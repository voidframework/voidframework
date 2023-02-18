package dev.voidframework.persistence.hibernate.annotation;

import dev.voidframework.persistence.hibernate.cuid.CUIDIdentifierGenerator;
import org.hibernate.annotations.IdGeneratorType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * CUID generation strategies for the values of primary keys.
 *
 * @since 1.3.0
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@IdGeneratorType(CUIDIdentifierGenerator.class)
public @interface CuidGenerator {
}
