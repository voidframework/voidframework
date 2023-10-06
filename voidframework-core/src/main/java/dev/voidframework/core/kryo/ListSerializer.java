package dev.voidframework.core.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.ImmutableSerializer;

import java.util.List;

/**
 * Kryo serializer for Java {@code List}.
 *
 * @since 1.11.0
 */
public final class ListSerializer extends ImmutableSerializer<List<Object>> {

    @Override
    public void write(final Kryo kryo, final Output output, final List<Object> objectList) {

        output.writeInt(objectList.size(), true);
        for (final Object obj : objectList) {
            kryo.writeClassAndObject(output, obj);
        }
    }

    @Override
    public List<Object> read(final Kryo kryo, final Input input, final Class<? extends List<Object>> type) {

        final int size = input.readInt(true);

        final Object[] objectArray = new Object[size];
        for (int idx = 0; idx < size; idx += 1) {
            objectArray[idx] = kryo.readClassAndObject(input);
        }

        return List.of(objectArray);
    }
}
