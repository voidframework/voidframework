package dev.voidframework.core.utils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferOutputStream;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import dev.voidframework.core.kryo.ListSerializer;
import dev.voidframework.core.kryo.SetSerializer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Kryo serializer utility methods.
 *
 * @since 1.11.0
 */
public final class KryoUtils {

    private static final byte[] EMPTY_ARRAY = new byte[0];
    private static final Kryo KRYO = new Kryo();

    static {

        // Configures Kryo
        KRYO.setRegistrationRequired(false);

        // Registers primitive types
        KRYO.register(boolean.class);
        KRYO.register(double.class);
        KRYO.register(float.class);
        KRYO.register(int.class);
        KRYO.register(long.class);

        // Registers types
        KRYO.register(ArrayList.class);
        KRYO.register(BigDecimal.class);
        KRYO.register(BigInteger.class);
        KRYO.register(Boolean.class);
        KRYO.register(Class.class);
        KRYO.register(Double.class);
        KRYO.register(EnumMap.class);
        KRYO.register(EnumSet.class);
        KRYO.register(Float.class);
        KRYO.register(HashMap.class);
        KRYO.register(HashSet.class);
        KRYO.register(Integer.class);
        KRYO.register(LinkedHashMap.class);
        KRYO.register(LinkedHashSet.class);
        KRYO.register(Long.class);
        KRYO.register(Optional.class);
        KRYO.register(String.class);

        // Adds new serializers (for types with no-args constructor)
        KRYO.addDefaultSerializer(List.class, ListSerializer.class);
        KRYO.addDefaultSerializer(Set.class, SetSerializer.class);
    }

    /**
     * Default constructor.
     *
     * @since 1.11.0
     */
    private KryoUtils() {

        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Deserialize an object.
     *
     * @param serializedContent An array of bytes representing a serialized object
     * @param classType         The object class type
     * @param <T>               The type of the Java object
     * @return Deserialized object
     * @since 1.11.0
     */
    public static <T> T deserialize(final byte[] serializedContent, final Class<T> classType) {

        if (serializedContent == null || serializedContent.length == 0) {
            return null;
        }

        final Input input = new Input(serializedContent);
        return KRYO.readObjectOrNull(input, classType);
    }

    /**
     * Serialize an object.
     *
     * @param object The object to serialize
     * @return An array of bytes representing the serialized object
     * @since 1.11.0
     */
    public static byte[] serialize(final Object object) {

        final Output output = new Output(new ByteBufferOutputStream());
        final Class<?> classType = object != null ? object.getClass() : Object.class;

        KRYO.writeObjectOrNull(output, object, classType);
        return output.toBytes();
    }

    /**
     * Deserialize an object.
     * This method will return {@code null} if an exception is thrown.
     *
     * @param serializedContent An array of bytes representing a serialized object
     * @param classType         The object class type
     * @param <T>               The type of the Java object
     * @return Deserialized object
     * @since 1.11.0
     */
    public static <T> T deserializeWithoutException(final byte[] serializedContent, final Class<T> classType) {

        try {
            return deserialize(serializedContent, classType);
        } catch (final Exception ignore) {
            return null;
        }
    }

    /**
     * Serialize an object.
     * This method will return an empty array if an exception is thrown.
     *
     * @param object The object to serialize
     * @return An array of bytes representing the serialized object
     * @since 1.11.0
     */
    public static byte[] serializeWithoutException(final Object object) {

        try {
            return serialize(object);
        } catch (final Exception ignore) {
            return EMPTY_ARRAY;
        }
    }

    /**
     * Gets the Kryo instance.
     *
     * @return Kryo instance
     * @since 1.11.0
     */
    public static Kryo kryo() {

        return KRYO;
    }
}
