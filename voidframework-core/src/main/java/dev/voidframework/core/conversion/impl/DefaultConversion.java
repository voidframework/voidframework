package dev.voidframework.core.conversion.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.voidframework.core.conversion.Conversion;
import dev.voidframework.core.conversion.ConverterManager;
import dev.voidframework.core.conversion.TypeConverter;
import dev.voidframework.core.exception.ConversionException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Default implementation of {@link Conversion}.
 *
 * @since 1.0.0
 */
@Singleton
@SuppressWarnings("java:S1168")
public class DefaultConversion implements Conversion {

    private final ConverterManager converterManager;

    /**
     * Build a new instance.
     *
     * @param converterManager Instance of the Converter Manager
     * @since 1.0.0
     */
    @Inject
    public DefaultConversion(final ConverterManager converterManager) {

        this.converterManager = converterManager;
    }

    @Override
    public <S, T> boolean canConvert(final Class<S> sourceTypeClass, final Class<T> targetTypeClass) {

        return converterManager.hasConvertFor(sourceTypeClass, targetTypeClass);
    }

    @Override
    public <T> boolean canConvert(final Object object, final Class<T> targetTypeClass) {

        if (object == null) {
            // null value can always be converted
            return true;
        }

        return converterManager.hasConvertFor(object.getClass(), targetTypeClass);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S, T> T convert(final S object, final Class<T> targetTypeClass) {

        if (object == null) {
            return null;
        }

        return convert(object, (Class<S>) object.getClass(), targetTypeClass);
    }

    @Override
    public <S, T> T convert(final S object, final Class<S> sourceTypeClass, final Class<T> targetTypeClass) {

        if (object == null) {
            return null;
        }

        final TypeConverter<S, T> converter = converterManager.getConverter(
            sourceTypeClass,
            targetTypeClass);

        if (converter == null) {
            throw new ConversionException.ConverterDoesNotExist(sourceTypeClass, targetTypeClass);
        }

        return converter.convert(object);
    }

    @Override
    public <S, T> List<T> convert(final Iterable<S> objectIterable, final Class<T> targetTypeClass) {

        if (objectIterable == null) {
            return null;
        }

        final List<T> targetList = new ArrayList<>();
        convertIntoCollection(objectIterable, targetList, targetTypeClass);

        return targetList;
    }

    @Override
    public <S, T> List<T> convert(final Iterable<S> objectIterable, final Class<S> sourceTypeClass, final Class<T> targetTypeClass) {

        if (objectIterable == null) {
            return null;
        }

        final List<T> targetList = new ArrayList<>();
        convertIntoCollection(objectIterable, targetList, sourceTypeClass, targetTypeClass);

        return targetList;
    }

    @Override
    public <S, T> List<T> convert(final List<S> objectList, final Class<T> targetTypeClass) {

        if (objectList == null) {
            return null;
        } else if (objectList.isEmpty()) {
            return Collections.emptyList();
        }

        final List<T> targetList = new ArrayList<>();
        convertIntoCollection(objectList, targetList, targetTypeClass);

        return targetList;
    }

    @Override
    public <S, T> List<T> convert(final List<S> objectList, final Class<S> sourceTypeClass, final Class<T> targetTypeClass) {

        if (objectList == null) {
            return null;
        } else if (objectList.isEmpty()) {
            return Collections.emptyList();
        }

        final List<T> targetList = new ArrayList<>();
        convertIntoCollection(objectList, targetList, sourceTypeClass, targetTypeClass);

        return targetList;
    }

    @Override
    public <S, T> Set<T> convert(final Set<S> objectSet, final Class<T> targetTypeClass) {

        if (objectSet == null) {
            return null;
        } else if (objectSet.isEmpty()) {
            return Collections.emptySet();
        }

        final Set<T> targetSet = new HashSet<>();
        convertIntoCollection(objectSet, targetSet, targetTypeClass);

        return targetSet;
    }

    @Override
    public <S, T> Set<T> convert(final Set<S> objectSet, final Class<S> sourceTypeClass, final Class<T> targetTypeClass) {

        if (objectSet == null) {
            return null;
        } else if (objectSet.isEmpty()) {
            return Collections.emptySet();
        }

        final Set<T> targetSet = new HashSet<>();
        convertIntoCollection(objectSet, targetSet, sourceTypeClass, targetTypeClass);

        return targetSet;
    }

    /**
     * Convert each object from the source iterable and save it into the target collection.
     *
     * @param objectSourceIterable   The source iterable
     * @param objectTargetCollection The target collection
     * @param targetTypeClass        The target type class
     * @param <S>                    The source generic type
     * @param <T>                    The target generic type
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    private <S, T> void convertIntoCollection(final Iterable<S> objectSourceIterable,
                                              final Collection<T> objectTargetCollection,
                                              final Class<T> targetTypeClass) {

        TypeConverter<S, T> converter = null;

        for (final S object : objectSourceIterable) {
            if (object == null) {
                // Object is null, no converter needed
                objectTargetCollection.add(null);
            } else if (converter != null) {
                // Converter already resolved, use it
                objectTargetCollection.add(converter.convert(object));
            } else {
                // Resolve converter and use it
                final Class<S> sourceTypeClass = (Class<S>) object.getClass();
                converter = converterManager.getConverter(sourceTypeClass, targetTypeClass);

                if (converter == null) {
                    throw new ConversionException.ConverterDoesNotExist(sourceTypeClass, targetTypeClass);
                }

                objectTargetCollection.add(converter.convert(object));
            }
        }
    }

    /**
     * Convert each object from the source iterable and save it into the target collection.
     *
     * @param objectSourceIterable   The source iterable
     * @param objectTargetCollection The target collection
     * @param sourceTypeClass        The source type class
     * @param targetTypeClass        The target type class
     * @param <S>                    The source generic type
     * @param <T>                    The target generic type
     * @since 1.0.0
     */
    private <S, T> void convertIntoCollection(final Iterable<S> objectSourceIterable,
                                              final Collection<T> objectTargetCollection,
                                              final Class<S> sourceTypeClass,
                                              final Class<T> targetTypeClass) {

        final TypeConverter<S, T> converter = converterManager.getConverter(
            sourceTypeClass,
            targetTypeClass);

        if (converter == null) {
            throw new ConversionException.ConverterDoesNotExist(sourceTypeClass, targetTypeClass);
        }

        for (final S object : objectSourceIterable) {
            if (object != null) {
                objectTargetCollection.add(converter.convert(object));
            } else {
                objectTargetCollection.add(null);
            }
        }
    }
}
