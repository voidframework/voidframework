package dev.voidframework.core;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.typesafe.config.Config;
import dev.voidframework.core.classestoload.ConverterInformation;
import dev.voidframework.core.classestoload.ScannedClassesToLoad;
import dev.voidframework.core.conditionalfeature.ConditionalFeatureVerifier;
import org.aspectj.lang.Aspects;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This module is in charge tobBind scanned classes.
 *
 * @since 1.9.0
 */
final class ScanClassBindModule extends AbstractModule {

    private final Config configuration;
    private final ConditionalFeatureVerifier conditionalFeatureVerifier;
    private final ScannedClassesToLoad scannedClassesToLoad;
    private final Map<Class<?>, Multibinder<?>> multibinderMap;

    /**
     * Build a new instance.
     *
     * @param configuration              The application configuration
     * @param conditionalFeatureVerifier Instance of the conditional feature verifier
     * @param scannedClassesToLoad       Scanned classes to load
     * @since 1.9.0
     */
    ScanClassBindModule(final Config configuration,
                        final ConditionalFeatureVerifier conditionalFeatureVerifier,
                        final ScannedClassesToLoad scannedClassesToLoad) {

        this.configuration = configuration;
        this.conditionalFeatureVerifier = conditionalFeatureVerifier;
        this.scannedClassesToLoad = scannedClassesToLoad;

        this.multibinderMap = new HashMap<>();
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void configure() {

        if (this.configuration.getBoolean("voidframework.core.requireExplicitBindings")) {
            this.binder().requireExplicitBindings();
        }

        for (final Class<?> classType : this.scannedClassesToLoad.bindableList()) {
            if (this.conditionalFeatureVerifier.isFeatureDisabled(classType)) {
                continue;
            }

            this.bind(classType);

            for (final Class<?> interfaceClassType : classType.getInterfaces()) {
                this.multibinderMap.computeIfAbsent(interfaceClassType, key -> Multibinder.newSetBinder(binder(), interfaceClassType))
                    .addBinding()
                    .to((Class) classType);


                if (Objects.equals(this.scannedClassesToLoad.interfaceImplementationCountMap().get(interfaceClassType), 1)) {
                    this.bind(interfaceClassType).to((Class) classType);
                }
            }
        }

        for (final Class<?> classType : this.scannedClassesToLoad.aspectList()) {
            if (this.conditionalFeatureVerifier.isFeatureDisabled(classType)) {
                continue;
            }

            this.requestInjection(Aspects.aspectOf(classType));
        }

        if (this.configuration.getBoolean("voidframework.core.requireExplicitBindings")) {
            for (final ConverterInformation converterInformation : this.scannedClassesToLoad.converterInformationList()) {
                this.bind(converterInformation.converterTypeClass());
            }
        }
    }
}
