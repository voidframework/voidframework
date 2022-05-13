package com.voidframework.core.server;

/**
 * Server listener information.
 *
 * @param protocol The protocol (ie: http)
 * @param host     The host with the listen port (ie: /127.0.0.1:8080)
 */
public record ListenerInformation(String protocol, String host) {

    @Override
    public String toString() {
        return protocol + (host != null && host.charAt(0) == '/' ? ":/" : "://") + host;
    }
}
