package com.voidframework.core.conversion.impl;

import com.voidframework.core.conversion.Conversion;
import com.voidframework.core.conversion.ConverterManager;
import com.voidframework.core.conversion.TypeConverter;
import com.voidframework.core.exception.ConversionException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Default implementation of {@link Conversion}.
 */
@Singleton
public class DefaultConversion implements Conversion {

    private final ConverterManager converterManager;

    /**
     * Build a new instance.
     *
     * @param converterManager Instance of the Converter Manager
     */
    @Inject
    public DefaultConversion(final ConverterManager converterManager) {
        this.converterManager = converterManager;
    }

    @Override
    public <SOURCE_TYPE, TARGET_TYPE> boolean canConvert(final Class<SOURCE_TYPE> sourceTypeClass,
                                                         final Class<TARGET_TYPE> targetTypeClass) {
        return converterManager.hasConvertFor(sourceTypeClass, targetTypeClass);
    }

    @Override
    public <TARGET_TYPE> boolean canConvert(final Object object,
                                            final Class<TARGET_TYPE> targetTypeClass) {
        if (object == null) {
            // null value can always be converted
            return true;
        }

        return converterManager.hasConvertFor(object.getClass(), targetTypeClass);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <SOURCE_TYPE, TARGET_TYPE> TARGET_TYPE convert(final SOURCE_TYPE object,
                                                          final Class<TARGET_TYPE> targetTypeClass) {
        if (object == null) {
            return null;
        }

        return convert(object, (Class<SOURCE_TYPE>) object.getClass(), targetTypeClass);
    }

    @Override
    public <SOURCE_TYPE, TARGET_TYPE> TARGET_TYPE convert(final SOURCE_TYPE object,
                                                          final Class<SOURCE_TYPE> sourceTypeClass,
                                                          final Class<TARGET_TYPE> targetTypeClass) {
        if (object == null) {
            return null;
        }

        final TypeConverter<SOURCE_TYPE, TARGET_TYPE> converter = converterManager.getConverter(
            sourceTypeClass,
            targetTypeClass);

        if (converter == null) {
            throw new ConversionException.ConverterDoesNotExist(sourceTypeClass, targetTypeClass);
        }

        return converter.convert(object);
    }

    @Override
    public <SOURCE_TYPE, TARGET_TYPE> List<TARGET_TYPE> convert(final Iterable<SOURCE_TYPE> objectIterable,
                                                                final Class<TARGET_TYPE> targetTypeClass) {
        if (objectIterable == null) {
            return null;
        }

        final List<TARGET_TYPE> targetList = new ArrayList<>();
        convertIntoCollection(objectIterable, targetList, targetTypeClass);

        return targetList;
    }

    @Override
    public <SOURCE_TYPE, TARGET_TYPE> List<TARGET_TYPE> convert(final Iterable<SOURCE_TYPE> objectIterable,
                                                                final Class<SOURCE_TYPE> sourceTypeClass,
                                                                final Class<TARGET_TYPE> targetTypeClass) {
        if (objectIterable == null) {
            return null;
        }

        final List<TARGET_TYPE> targetList = new ArrayList<>();
        convertIntoCollection(objectIterable, targetList, sourceTypeClass, targetTypeClass);

        return targetList;
    }

    @Override
    public <SOURCE_TYPE, TARGET_TYPE> List<TARGET_TYPE> convert(final List<SOURCE_TYPE> objectList,
                                                                final Class<TARGET_TYPE> targetTypeClass) {
        if (objectList == null) {
            return null;
        }
        if (objectList.isEmpty()) {
            return Collections.emptyList();
        }

        final List<TARGET_TYPE> targetList = new ArrayList<>();
        convertIntoCollection(objectList, targetList, targetTypeClass);

        return targetList;
    }

    @Override
    public <SOURCE_TYPE, TARGET_TYPE> List<TARGET_TYPE> convert(final List<SOURCE_TYPE> objectList,
                                                                final Class<SOURCE_TYPE> sourceTypeClass,
                                                                final Class<TARGET_TYPE> targetTypeClass) {
        if (objectList == null) {
            return null;
        }
        if (objectList.isEmpty()) {
            return Collections.emptyList();
        }

        final List<TARGET_TYPE> targetList = new ArrayList<>();
        convertIntoCollection(objectList, targetList, sourceTypeClass, targetTypeClass);

        return targetList;
    }

    @Override
    public <SOURCE_TYPE, TARGET_TYPE> Set<TARGET_TYPE> convert(final Set<SOURCE_TYPE> objectSet,
                                                               final Class<TARGET_TYPE> targetTypeClass) {
        if (objectSet == null) {
            return null;
        }
        if (objectSet.isEmpty()) {
            return Collections.emptySet();
        }

        final Set<TARGET_TYPE> targetSet = new HashSet<>();
        convertIntoCollection(objectSet, targetSet, targetTypeClass);

        return targetSet;
    }

    @Override
    public <SOURCE_TYPE, TARGET_TYPE> Set<TARGET_TYPE> convert(final Set<SOURCE_TYPE> objectSet,
                                                               final Class<SOURCE_TYPE> sourceTypeClass,
                                                               final Class<TARGET_TYPE> targetTypeClass) {
        if (objectSet == null) {
            return null;
        }
        if (objectSet.isEmpty()) {
            return Collections.emptySet();
        }

        final Set<TARGET_TYPE> targetSet = new HashSet<>();
        convertIntoCollection(objectSet, targetSet, sourceTypeClass, targetTypeClass);

        return targetSet;
    }

    /**
     * Convert each object from the source iterable and save it into the target collection.
     *
     * @param objectSourceIterable   The source iterable
     * @param objectTargetCollection The target collection
     * @param targetTypeClass        The target type class
     * @param <SOURCE_TYPE>          The source generic type
     * @param <TARGET_TYPE>          The target generic type
     */
    @SuppressWarnings("unchecked")
    private <SOURCE_TYPE, TARGET_TYPE> void convertIntoCollection(final Iterable<SOURCE_TYPE> objectSourceIterable,
                                                                  final Collection<TARGET_TYPE> objectTargetCollection,
                                                                  final Class<TARGET_TYPE> targetTypeClass) {

        TypeConverter<SOURCE_TYPE, TARGET_TYPE> converter = null;

        for (final SOURCE_TYPE object : objectSourceIterable) {
            if (object == null) {
                // Object is null, no converter needed
                objectTargetCollection.add(null);
            } else if (converter != null) {
                // Converter already resolved, use it
                objectTargetCollection.add(converter.convert(object));
            } else {
                // Resolve converter and use it
                final Class<SOURCE_TYPE> sourceTypeClass = (Class<SOURCE_TYPE>) object.getClass();
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
     * @param <SOURCE_TYPE>          The source generic type
     * @param <TARGET_TYPE>          The target generic type
     */

    private <SOURCE_TYPE, TARGET_TYPE> void convertIntoCollection(final Iterable<SOURCE_TYPE> objectSourceIterable,
                                                                  final Collection<TARGET_TYPE> objectTargetCollection,
                                                                  final Class<SOURCE_TYPE> sourceTypeClass,
                                                                  final Class<TARGET_TYPE> targetTypeClass) {
        final TypeConverter<SOURCE_TYPE, TARGET_TYPE> converter = converterManager.getConverter(
            sourceTypeClass,
            targetTypeClass);

        if (converter == null) {
            throw new ConversionException.ConverterDoesNotExist(sourceTypeClass, targetTypeClass);
        }

        for (final SOURCE_TYPE object : objectSourceIterable) {
            if (object != null) {
                objectTargetCollection.add(converter.convert(object));
            } else {
                objectTargetCollection.add(null);
            }
        }
    }
}
