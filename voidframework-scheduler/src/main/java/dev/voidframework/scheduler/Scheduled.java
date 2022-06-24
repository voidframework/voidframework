package dev.voidframework.scheduler;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the method should be periodically called.
 * The annotated method must expect no arguments.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Scheduled {

    /**
     * The time in milliseconds between the end of the last execution and the next execution.
     *
     * @return The time in milliseconds
     */
    int fixedDelay() default 0;

    /**
     * The time in milliseconds between each execution.
     *
     * @return The time in milliseconds
     */
    int fixedRate() default 0;

    /**
     * The time in milliseconds to wait before the first execution of {@link #fixedDelay()} and
     * {@link #fixedRate()}. If not specified, the {@link #fixedRate()} value will be used.
     *
     * @return The initial delay
     */
    int initialDelay() default 0;

    /**
     * A CRON-like expression containing the second, minute, hour, day of month, month, and day of week.
     *
     * @return The CRON expression
     */
    String cron() default "";

    /**
     * A time zone for which the CRON expression will be resolved. If not specified, the server's local time
     * zone will be used.
     *
     * @return A zone id accepted by {@code TimeZone.getTimeZone(String)}
     */
    String cronZone() default "UTC";
}
