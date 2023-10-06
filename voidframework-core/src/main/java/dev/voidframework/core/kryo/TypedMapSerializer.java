package dev.voidframework.core.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.ImmutableSerializer;
import dev.voidframework.core.lang.TypedMap;
import dev.voidframework.core.utils.ReflectionUtils;

import java.util.Map;

/**
 * Kryo serializer for Java {@code TypedMap}.
 *
 * @since 1.11.0
 */
public final class TypedMapSerializer extends ImmutableSerializer<TypedMap> {

    @Override
    public void write(final Kryo kryo, final Output output, final TypedMap typedMap) {

        final Map<TypedMap.Key<?>, ?> internalMap = ReflectionUtils.getFieldValue(
            typedMap,
            "internalMap",
            new ReflectionUtils.WrappedClass<>());

        if (internalMap != null) {
            output.writeInt(internalMap.size());
            for (final Map.Entry<TypedMap.Key<?>, ?> entry : internalMap.entrySet()) {
                kryo.writeClassAndObject(output, entry.getKey());
                kryo.writeClassAndObject(output, entry.getValue());
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public TypedMap read(final Kryo kryo, final Input input, final Class<? extends TypedMap> type) {

        final TypedMap typedMap = new TypedMap();
        final int size = input.readInt();

        for (int idx = 0; idx < size; idx += 1) {
            typedMap.put(
                (TypedMap.Key<Object>) kryo.readClassAndObject(input),
                kryo.readClassAndObject(input));
        }


        return typedMap;
    }
}
