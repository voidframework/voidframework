package sample.scheduler;

import dev.voidframework.core.bindable.BindClass;
import dev.voidframework.scheduler.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scheduled ping.
 */
@BindClass
public final class Ping {

    private static final Logger LOGGER = LoggerFactory.getLogger(Ping.class);

    /**
     * Ping.
     */
    @Scheduled(cron = "0 */5 * * * *")
    public void doPing() {
        LOGGER.info("PING!!");
    }
}
