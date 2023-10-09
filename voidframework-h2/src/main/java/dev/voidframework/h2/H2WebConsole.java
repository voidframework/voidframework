package dev.voidframework.h2;

import com.typesafe.config.Config;
import dev.voidframework.core.lifecycle.LifeCycleStart;
import dev.voidframework.core.lifecycle.LifeCycleStop;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.h2.server.web.WebServer;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * H2 Web Console.
 */
@Singleton
public final class H2WebConsole {

    private static final Logger LOGGER = LoggerFactory.getLogger(H2WebConsole.class);

    private final Config configuration;

    private Server server;

    /**
     * Build a new instance.
     *
     * @param configuration The application configuration
     */
    @Inject
    public H2WebConsole(final Config configuration) {

        this.configuration = configuration;
    }

    /**
     * Starts H2 web console.
     *
     * @throws SQLException If H2 web console can't be started
     */
    @LifeCycleStart(priority = 1000000)
    public void start() throws SQLException {

        final List<String> argumentList = new ArrayList<>();
        argumentList.add("-webPort");
        argumentList.add(String.valueOf(this.configuration.getInt("voidframework.h2.webPort")));

        if (this.configuration.hasPath("voidframework.h2.webAdminPassword")) {
            argumentList.add("-webAdminPassword");
            argumentList.add(
                WebServer.encodeAdminPassword(this.configuration.getString("voidframework.h2.webAdminPassword")));
        }

        if (this.configuration.getBoolean("voidframework.h2.webAllowOthers")) {
            argumentList.add("-webAllowOthers");
        }

        this.server = Server.createWebServer(argumentList.toArray(String[]::new));
        this.server.start();

        final String listenAddress;
        if (this.server.getService().getAllowOthers()) {
            listenAddress = this.server.getService().getURL();
        } else {
            listenAddress = "http://127.0.0.1:" + this.server.getPort();
        }
        LOGGER.info("H2 Web Console now listening on {}/", listenAddress);
    }

    /**
     * Stops H2 web console.
     */
    @LifeCycleStop(priority = 1000000)
    public void stop() {

        if (this.server != null) {
            this.server.stop();
        }
    }
}
