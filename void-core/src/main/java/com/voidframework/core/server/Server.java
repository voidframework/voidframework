package com.voidframework.core.server;

import com.typesafe.config.Config;
import org.slf4j.Logger;


public interface Server {

    void run(final Config configuration, final Logger logger);
}
