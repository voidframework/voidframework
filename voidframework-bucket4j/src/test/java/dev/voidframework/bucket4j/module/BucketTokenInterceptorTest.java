package dev.voidframework.bucket4j.module;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.bucket4j.BucketTokenRegistry;
import dev.voidframework.bucket4j.annotation.BucketToken;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class BucketTokenInterceptorTest {

    @Test
    void invoke_primaryMethod() throws Throwable {

        // Arrange
        final Config configuration = ConfigFactory.parseString("""
            voidframework.bucket4j.bucketAPI1.synchronizationStrategy = "LOCK_FREE"

            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.id = "one"
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.capacity = 60
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.refill.strategy = "GREEDY"
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.refill.tokens = 60
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.refill.period = "1 minutes"
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.refill.initialTokens = 24
            """);
        final BucketTokenRegistry bucketTokenRegistry = new BucketTokenRegistry(configuration);
        final BucketTokenInterceptor interceptor = new BucketTokenInterceptor(bucketTokenRegistry);

        final DummService dummService = new DummService();
        final Method methodPrimary = DummService.class.getDeclaredMethod("primary");
        final MethodInvocation methodInvocation = new FakeMethodInvocation(dummService, methodPrimary);

        // Act
        final Object value = interceptor.invoke(methodInvocation);

        // Assert
        Assertions.assertNotNull(value);
        Assertions.assertEquals("PRIMARY", value);
    }

    @Test
    void invoke_fallbackMethod() throws Throwable {

        // Arrange
        final Config configuration = ConfigFactory.parseString("""
            voidframework.bucket4j.bucketAPI1.synchronizationStrategy = "LOCK_FREE"

            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.id = "one"
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.capacity = 60
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.refill.strategy = "GREEDY"
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.refill.tokens = 60
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.refill.period = "24 hours"
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.refill.initialTokens = 0
            """);
        final BucketTokenRegistry bucketTokenRegistry = new BucketTokenRegistry(configuration);
        final BucketTokenInterceptor interceptor = new BucketTokenInterceptor(bucketTokenRegistry);

        final DummService dummService = new DummService();
        final Method methodPrimary = DummService.class.getDeclaredMethod("primary");
        final MethodInvocation methodInvocation = new FakeMethodInvocation(dummService, methodPrimary);

        // Act
        final Object value = interceptor.invoke(methodInvocation);

        // Assert
        Assertions.assertNotNull(value);
        Assertions.assertEquals( "FALLBACK", value);
    }

    /**
     * A fake method invocation.
     */
    private static final class FakeMethodInvocation implements MethodInvocation {

        private final Object owner;
        private final Method method;
        private final Object[] arguments;

        /**
         * Build a new instance
         *
         * @param owner  Instance of the object containing the method
         * @param method The called method
         */
        public FakeMethodInvocation(final Object owner,
                                    final Method method) {

            this(owner, method, new Object[0]);
        }

        /**
         * Build a new instance
         *
         * @param owner     Instance of the object containing the method
         * @param method    The called method
         * @param arguments The method arguments
         */
        public FakeMethodInvocation(final Object owner,
                                    final Method method,
                                    final Object[] arguments) {

            this.owner = owner;
            this.method = method;
            this.arguments = arguments;
        }

        @Override
        public Method getMethod() {

            return method;
        }

        @Override
        public Object[] getArguments() {

            return arguments;
        }

        @Override
        public Object proceed() throws Throwable {

            return method.invoke(owner, arguments);
        }

        @Override
        public Object getThis() {

            return owner;
        }

        @Override
        public AccessibleObject getStaticPart() {

            throw new UnsupportedOperationException();
        }
    }

    /**
     * Dummy service.
     */
    private static final class DummService {

        public String accept() {

            // This method is here to simulate the fact that fallback
            // method is not found at the first foreach turn
            return "ACCEPT";
        }

        public String done() {

            // This method is here to simulate the fact that fallback
            // method is not found at the first foreach turn
            return "DONE";
        }

        public String fallback() {

            return "FALLBACK";
        }

        @BucketToken(value = "bucketAPI1", fallbackMethod = "fallback")
        public String primary() {

            return "PRIMARY";
        }
    }
}
