package dev.voidframework.core.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import dev.voidframework.core.lang.CUID;

/**
 * Void Framework module for Jackson.
 *
 * @since 1.3.0
 */
public class VoidFrameworkModule extends SimpleModule {

    /**
     * Build a new instance.
     *
     * @since 1.3.0
     */
    public VoidFrameworkModule() {

        super("VoidFrameworkModule", Version.unknownVersion());

        this.addSerializer(CUID.class, new CUIDSerializer());
        this.addDeserializer(CUID.class, new CUIDDeserializer());
    }
}
