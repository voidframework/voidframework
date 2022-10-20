package dev.voidframework.persistence.hibernate.cuid;

import dev.voidframework.core.lang.CUID;
import dev.voidframework.persistence.hibernate.annotation.CuidGenerator;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.spi.CustomIdGeneratorCreationContext;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

/**
 * CUID-based IdentifierGenerator.
 *
 * @see CuidGenerator
 */
public final class CUIDIdentifierGenerator implements IdentifierGenerator {

    private final Supplier<Object> cuidSupplier;

    /**
     * Build a new instance
     *
     * @param config          The generator configuration
     * @param annotatedMember The member annotated with {@code @CUIDGeneratedValue}
     * @param context         The context
     */
    public CUIDIdentifierGenerator(final CuidGenerator config,
                                   final Member annotatedMember,
                                   final CustomIdGeneratorCreationContext context) {

        final Class<?> memberClassType;
        if (annotatedMember instanceof Method) {
            memberClassType = ((Method) annotatedMember).getReturnType();
        } else {
            memberClassType = ((Field) annotatedMember).getType();
        }

        if (CUID.class.isAssignableFrom(memberClassType)) {
            this.cuidSupplier = CUIDIdentifierGenerator::generateAsCUID;
        } else if (String.class.isAssignableFrom(memberClassType)) {
            this.cuidSupplier = CUIDIdentifierGenerator::generateAsString;
        } else if (byte[].class.isAssignableFrom(memberClassType)) {
            this.cuidSupplier = CUIDIdentifierGenerator::generateAsByteArray;
        } else {
            throw new HibernateException("Unanticipated return type '" + memberClassType.getName() + "' for CUID conversion");
        }
    }

    /**
     * Generates a random CUID.
     *
     * @return Newly generated CUID
     */
    private static CUID generateAsCUID() {

        return CUID.randomCUID();
    }

    /**
     * Generates a random CUID.
     *
     * @return Newly generated CUID as String
     */
    private static String generateAsString() {

        return CUID.randomCUID().toString();
    }

    /**
     * Generates a random CUID.
     *
     * @return Newly generated CUID as byte array
     */
    private static byte[] generateAsByteArray() {

        return CUID.randomCUID().toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Object generate(final SharedSessionContractImplementor sharedSessionContractImplementor,
                           final Object o) throws HibernateException {

        return this.cuidSupplier.get();
    }
}
