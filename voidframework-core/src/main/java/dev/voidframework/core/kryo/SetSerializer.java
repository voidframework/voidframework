package dev.voidframework.core.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.ImmutableSerializer;

import java.util.Set;

/**
 * Kryo serializer for Java {@code Set}.
 *
 * @since 1.11.0
 */
public final class SetSerializer extends ImmutableSerializer<Set<Object>> {

    @Override
    public void write(final Kryo kryo, final Output output, final Set<Object> objectSet) {

        output.writeInt(objectSet.size(), true);
        for (final Object obj : objectSet) {
            kryo.writeClassAndObject(output, obj);
        }
    }

    @Override
    public Set<Object> read(final Kryo kryo, final Input input, final Class<? extends Set<Object>> type) {

        final int size = input.readInt(true);

        final Object[] objectArray = new Object[size];
        for (int idx = 0; idx < size; idx += 1) {
            objectArray[idx] = kryo.readClassAndObject(input);
        }

        return Set.of(objectArray);
    }
}
