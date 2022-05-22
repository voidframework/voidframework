package com.voidframework.core.conversion.impl;

import com.google.inject.Singleton;
import com.voidframework.core.conversion.ConverterManager;
import com.voidframework.core.conversion.TypeConverter;
import com.voidframework.core.exception.ConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of {@link ConverterManager}.
 */
@Singleton
public final class DefaultConverterManager implements ConverterManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConverterManager.class);

    private final Map<ConverterCompositeKey, TypeConverter<?, ?>> converterMap;

    /**
     * Build a new instance.
     */
    public DefaultConverterManager() {
        this.converterMap = new HashMap<>();
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

        LOGGER.debug("Register new Converter<source={}, target={}>", sourceClassType, targetClassType);
        if (this.converterMap.put(new ConverterCompositeKey(sourceClassType, targetClassType), converter) != null) {
            throw new ConversionException.ConverterAlreadyRegistered(sourceClassType, targetClassType);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <SOURCE_TYPE, TARGET_TYPE>
    TypeConverter<SOURCE_TYPE, TARGET_TYPE> getConverter(final Class<SOURCE_TYPE> sourceClassType,
                                                         final Class<TARGET_TYPE> targetClassType) {

        return (TypeConverter<SOURCE_TYPE, TARGET_TYPE>) this.converterMap.get(
            new ConverterCompositeKey(sourceClassType, targetClassType));
    }

    @Override
    public int count() {
        return this.converterMap.size();
    }
}
