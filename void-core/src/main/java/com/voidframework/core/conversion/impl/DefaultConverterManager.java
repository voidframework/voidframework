package com.voidframework.core.conversion.impl;

import com.google.inject.Singleton;
import com.voidframework.core.conversion.ConverterManager;
import com.voidframework.core.conversion.TypeConverter;
import com.voidframework.core.conversion.converter.StringToBooleanConverter;
import com.voidframework.core.conversion.converter.StringToByteConverter;
import com.voidframework.core.conversion.converter.StringToCharacterConverter;
import com.voidframework.core.conversion.converter.StringToDoubleConverter;
import com.voidframework.core.conversion.converter.StringToFloatConverter;
import com.voidframework.core.conversion.converter.StringToIntegerConverter;
import com.voidframework.core.conversion.converter.StringToLongConverter;
import com.voidframework.core.conversion.converter.StringToShortConverter;
import com.voidframework.core.conversion.converter.StringToUUIDConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Default implementation of {@link ConverterManager}.
 */
@Singleton
public final class DefaultConverterManager implements ConverterManager {

    private final Map<ConverterCompositeKey, TypeConverter<?, ?>> converterMap;

    /**
     * Build a new instance.
     */
    public DefaultConverterManager() {
        this.converterMap = new HashMap<>();

        // Built-in converters
        this.converterMap.put(new ConverterCompositeKey(String.class, Boolean.class), new StringToBooleanConverter());
        this.converterMap.put(new ConverterCompositeKey(String.class, Byte.class), new StringToByteConverter());
        this.converterMap.put(new ConverterCompositeKey(String.class, Character.class), new StringToCharacterConverter());
        this.converterMap.put(new ConverterCompositeKey(String.class, Double.class), new StringToDoubleConverter());
        this.converterMap.put(new ConverterCompositeKey(String.class, Float.class), new StringToFloatConverter());
        this.converterMap.put(new ConverterCompositeKey(String.class, Integer.class), new StringToIntegerConverter());
        this.converterMap.put(new ConverterCompositeKey(String.class, Long.class), new StringToShortConverter());
        this.converterMap.put(new ConverterCompositeKey(String.class, Short.class), new StringToLongConverter());
        this.converterMap.put(new ConverterCompositeKey(String.class, UUID.class), new StringToUUIDConverter());
    }

    @Override
    public <SOURCE_TYPE, TARGET_TYPE> boolean hasConvertFor(final Class<SOURCE_TYPE> sourceClassType,
                                                            final Class<TARGET_TYPE> targetClassType) {

        return this.converterMap.containsKey(new ConverterCompositeKey(sourceClassType, targetClassType));
    }

    @Override
    public void registerConverter(final Class<?> sourceClassType,
                                  final Class<?> targetClassType,
                                  final TypeConverter<?, ?> converter) {

        this.converterMap.put(new ConverterCompositeKey(sourceClassType, targetClassType), converter);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <SOURCE_TYPE, TARGET_TYPE>
    TypeConverter<SOURCE_TYPE, TARGET_TYPE> getConverter(final Class<SOURCE_TYPE> sourceClassType,
                                                         final Class<TARGET_TYPE> targetClassType) {

        return (TypeConverter<SOURCE_TYPE, TARGET_TYPE>) this.converterMap.get(
            new ConverterCompositeKey(sourceClassType, targetClassType));
    }
}
