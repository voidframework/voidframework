package dev.voidframework.core.conversion.impl;

import com.google.inject.Singleton;
import dev.voidframework.core.conversion.ConverterManager;
import dev.voidframework.core.conversion.TypeConverter;
import dev.voidframework.core.exception.ConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of {@link ConverterManager}.
 */
@Singleton
public final class DefaultConverterManager implements ConverterManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultConverterManager.class);

    private final Map<ConverterCompositeKey, TypeConverter<?, ?>> converterMap;

    /**
     * Build a new instance.
     */
    public DefaultConverterManager() {

        this.converterMap = new HashMap<>();
    }

    @Override
    public <S, T> boolean hasConvertFor(final Class<S> sourceClassType, final Class<T> targetClassType) {

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
    public <S, T> TypeConverter<S, T> getConverter(final Class<S> sourceClassType, final Class<T> targetClassType) {

        return (TypeConverter<S, T>) this.converterMap.get(
            new ConverterCompositeKey(sourceClassType, targetClassType));
    }

    @Override
    public int count() {

        return this.converterMap.size();
    }
}
