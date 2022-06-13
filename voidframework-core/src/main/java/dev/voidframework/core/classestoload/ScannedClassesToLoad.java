package dev.voidframework.core.classestoload;

import java.util.List;

/**
 * Scanned classes to load into Guice.
 *
 * @param moduleList               The module classes list
 * @param bindableList             The bindable classes list
 * @param converterInformationList The converter information list
 */
public record ScannedClassesToLoad(List<Class<?>> moduleList,
                                   List<Class<?>> bindableList,
                                   List<ConverterInformation> converterInformationList) {

    /**
     * Returns the number of scanned classes.
     *
     * @return The number of scanned classes
     */
    public int count() {
        return moduleList.size() + bindableList.size() + converterInformationList.size();
    }
}
