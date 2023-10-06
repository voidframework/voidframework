package dev.voidframework.core.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.ImmutableSerializer;
import dev.voidframework.core.lang.TypedMap;
import dev.voidframework.core.utils.ReflectionUtils;

/**
 * Kryo serializer for Java {@code TypedMap.Key}.
 *
 * @since 1.11.0
 */
public final class TypedMapKeySerializer extends ImmutableSerializer<TypedMap.Key<Object>> {

    @Override
    public void write(final Kryo kryo, final Output output, final TypedMap.Key<Object> typedMapKey) {

        final String keyName = ReflectionUtils.getFieldValue(
            typedMapKey,
            "keyName",
            new ReflectionUtils.WrappedClass<>());
        final Class<?> valueClassType = ReflectionUtils.getFieldValue(
            typedMapKey,
            "valueClassType",
            new ReflectionUtils.WrappedClass<>());

        output.writeString(keyName);
        kryo.writeClassAndObject(output, valueClassType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public TypedMap.Key<Object> read(final Kryo kryo, final Input input, final Class<? extends TypedMap.Key<Object>> type) {

        final String keyName = input.readString();
        final Class<Object> valueClassType = (Class<Object>) kryo.readClassAndObject(input);

        return TypedMap.Key.of(keyName, valueClassType);
    }
}
