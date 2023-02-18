package dev.voidframework.core.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import dev.voidframework.core.lang.CUID;

import java.io.IOException;

/**
 * Jackson deserializer for {@code CUID}.
 *
 * @see CUID
 * @since 1.3.0
 */
public final class CUIDDeserializer extends StdDeserializer<CUID> {

    /**
     * Build a new instance.
     *
     * @since 1.3.0
     */
    public CUIDDeserializer() {

        this(null);
    }

    /**
     * Build a new instance.
     *
     * @param cuidClassType The CUID class type
     * @since 1.3.0
     */
    public CUIDDeserializer(final Class<CUID> cuidClassType) {

        super(cuidClassType);
    }

    @Override
    public CUID deserialize(final JsonParser jsonParser,
                            final DeserializationContext deserializationContext) throws IOException {

        return CUID.fromString(jsonParser.getText());
    }
}
