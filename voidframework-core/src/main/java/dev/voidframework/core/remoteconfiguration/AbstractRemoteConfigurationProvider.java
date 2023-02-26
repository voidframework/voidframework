package dev.voidframework.core.remoteconfiguration;

/**
 * This abstraction provides useful internal methods.
 *
 * @since 1.2.0
 */
public abstract class AbstractRemoteConfigurationProvider implements RemoteConfigurationProvider {

    /**
     * Checks if the given value represents a file or not.
     *
     * @param value The value to analyse
     * @return {@code true} if the value represents a file, otherwise, {@code false}
     * @since 1.2.0
     */
    protected boolean isFile(final String value) {

        if (!value.isEmpty() && value.charAt(0) == '"' && value.charAt(value.length() - 1) == '"') {
            return value
                .substring(1, value.length() - 1)
                .startsWith(FileCfgObject.FILE_MAGIC_ID);
        }

        return value.startsWith(FileCfgObject.FILE_MAGIC_ID);
    }
}
