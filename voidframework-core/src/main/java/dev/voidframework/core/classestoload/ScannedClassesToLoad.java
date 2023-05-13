package dev.voidframework.core.classestoload;

import java.util.List;
import java.util.Map;

/**
 * Scanned classes to load into Guice.
 *
 * @param moduleList                      The module classes list
 * @param bindableList                    The bindable classes list
 * @param proxyableList                   The proxyable interfaces list
 * @param converterInformationList        The converter information list
 * @param interfaceImplementationCountMap The interface implementation count map
 * @since 1.0.0
 */
public record ScannedClassesToLoad(List<Class<?>> moduleList,
                                   List<Class<?>> bindableList,
                                   List<Class<?>> proxyableList,
                                   List<ConverterInformation> converterInformationList,
                                   Map<Class<?>, Integer> interfaceImplementationCountMap) {

    /**
     * Returns the number of scanned classes detected as loadable.
     *
     * @return The number of scanned classes detected as loadable
     * @since 1.0.0
     */
    public int count() {

        return moduleList.size() + bindableList.size() + proxyableList.size() + converterInformationList.size();
    }
}
