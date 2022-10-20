package dev.voidframework.core.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import dev.voidframework.core.lang.CUID;

import java.io.IOException;

/**
 * Jackson serializer for {@code CUID}.
 *
 * @see CUID
 */
public final class CUIDSerializer extends StdSerializer<CUID> {

    /**
     * Build a new instance.
     */
    public CUIDSerializer() {

        this(null);
    }

    /**
     * Build a new instance.
     *
     * @param cuidClassType The CUID class type
     */
    public CUIDSerializer(final Class<CUID> cuidClassType) {

        super(cuidClassType);
    }

    @Override
    public void serialize(final CUID cuid,
                          final JsonGenerator jsonGenerator,
                          final SerializerProvider serializerProvider) throws IOException {

        if (cuid != null) {
            jsonGenerator.writeString(cuid.toString());
        } else {
            jsonGenerator.writeNull();
        }
    }
}
