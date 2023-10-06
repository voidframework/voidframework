package dev.voidframework.core.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.ImmutableSerializer;
import dev.voidframework.core.lang.Either;

/**
 * Kryo serializer for Java {@code Either}.
 *
 * @since 1.11.0
 */
public final class EitherSerializer extends ImmutableSerializer<Either<Object, Object>> {

    @Override
    public void write(final Kryo kryo, final Output output, final Either<Object, Object> either) {

        kryo.writeClassAndObject(output, either.getLeft());
        kryo.writeClassAndObject(output, either.getRight());
    }

    @Override
    public Either<Object, Object> read(final Kryo kryo, final Input input, final Class<? extends Either<Object, Object>> type) {

        final Object left = kryo.readClassAndObject(input);
        if (left != null) {
            return Either.ofLeft(left);
        }

        final Object right = kryo.readClassAndObject(input);
        return Either.ofRight(right);
    }
}
