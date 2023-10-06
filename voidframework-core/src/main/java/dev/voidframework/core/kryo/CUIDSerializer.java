package dev.voidframework.core.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.ImmutableSerializer;
import dev.voidframework.core.lang.CUID;

/**
 * Kryo serializer for Java {@code CUID}.
 *
 * @since 1.11.0
 */
public final class CUIDSerializer extends ImmutableSerializer<CUID> {

    @Override
    public void write(final Kryo kryo, final Output output, final CUID cuid) {

        output.writeString(cuid.toString());
    }

    @Override
    public CUID read(final Kryo kryo, final Input input, final Class<? extends CUID> type) {

        final String cuidAsString = input.readString();

        return CUID.fromString(cuidAsString);
    }
}
