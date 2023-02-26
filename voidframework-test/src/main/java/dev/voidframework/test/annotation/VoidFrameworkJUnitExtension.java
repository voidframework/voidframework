package dev.voidframework.test.annotation;

import com.google.inject.AbstractModule;
import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import dev.voidframework.core.VoidApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockSettings;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.internal.configuration.injection.scanner.InjectMocksScanner;
import org.mockito.internal.util.MockUtil;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * JUnit's extension to provide Void Framework context and Mockito annotations support.
 *
 * @since 1.2.0
 */
public class VoidFrameworkJUnitExtension implements TestInstancePostProcessor, AfterEachCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(VoidFrameworkJUnitExtension.class);

    private static final ExtensionContext.Namespace NAMESPACE_APP = ExtensionContext.Namespace.create("dev", "voidframework", "junit5", "app");

    private final Set<TrackedInstanceHandler<Object>> trackedInstanceHandlerSet = new HashSet<>();

    @Override
    public void postProcessTestInstance(final Object testInstance,
                                        final ExtensionContext context) throws Exception {

        // Mocks all "@Mock" annotated members. Must be done before calling "getOrCreateInjector"
        this.mockMemberAnnotatedWithMock(testInstance);

        // Retrieves injectors
        final Injector mockInjector = this.getOrCreateMockInjector();
        final Injector appInjector = this.getOrCreateApplicationInjector(context, testInstance);
        Assertions.assertNotNull(appInjector);

        // Injects all members
        this.injectMembers(appInjector, mockInjector, testInstance);
    }

    @Override
    public void afterEach(final ExtensionContext context) {

        for (final TrackedInstanceHandler<Object> mockedInstanceHandler : this.trackedInstanceHandlerSet) {
            Mockito.reset(mockedInstanceHandler.instance);
        }
    }

    /**
     * Retrieves or creates a new Mocked classes Injector.
     *
     * @return The Mocked classes injector
     * @since 1.2.0
     */
    private Injector getOrCreateMockInjector() {

        // Adds Guice module to register mocked instances
        final Module mockedValuesModule = new AbstractModule() {

            @Override
            protected void configure() {

                for (final TrackedInstanceHandler<Object> mockedInstanceHandler : trackedInstanceHandlerSet) {
                    bind(mockedInstanceHandler.classType).toInstance(mockedInstanceHandler.instance);
                }
            }
        };

        // Creates injector
        return Guice.createInjector(Stage.PRODUCTION, mockedValuesModule);
    }

    /**
     * Retrieves or creates a new Void Framework application / Injector.
     *
     * @param context      The JUnit extension context
     * @param testInstance The instance to post-process
     * @return The injector
     * @throws IllegalAccessException    If the Guice module constructor is not accessible
     * @throws InstantiationException    If the Guice module that declares the constructor represents an abstract class
     * @throws InvocationTargetException If the Guice module constructor throws an exception
     * @throws NoSuchMethodException     If the Guice module don't have valid constructor
     * @since 1.2.0
     */
    private Injector getOrCreateApplicationInjector(final ExtensionContext context,
                                                    final Object testInstance)
        throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {

        if (context.getElement().isEmpty()) {
            return null;
        }

        // Retrieves store
        final ExtensionContext.Store store = context.getStore(NAMESPACE_APP);

        // Gets or creates injector
        Injector injector = store.get(NAMESPACE_APP, Injector.class);
        if (injector == null) {
            final VoidApplication voidApplication = new VoidApplication();
            voidApplication.launch();

            // Adds Guice module to register mocked instances
            final List<Module> moduleList = new ArrayList<>();
            final Module mockedValuesModule = new AbstractModule() {

                @Override
                protected void configure() {

                    for (final TrackedInstanceHandler<Object> mockedInstanceHandler : trackedInstanceHandlerSet) {
                        bind(mockedInstanceHandler.classType).toInstance(mockedInstanceHandler.instance);
                    }
                }
            };

            moduleList.add(mockedValuesModule);

            // Adds extra modules (for the current test)
            final ExtraGuiceModule extraGuiceModule = testInstance.getClass().getAnnotation(ExtraGuiceModule.class);
            if (extraGuiceModule != null) {
                for (final Class<? extends Module> moduleClassType : extraGuiceModule.value()) {
                    final Module module = moduleClassType.getConstructor().newInstance();
                    moduleList.add(module);
                }
            }

            // Retrieves injector and applies new modules
            injector = voidApplication.getInstance(Injector.class).createChildInjector(moduleList);
            store.put(NAMESPACE_APP, injector);
        }

        return injector;
    }

    /**
     * Mocks "@Mock" annotated fields.
     *
     * @param testInstance The instance to post-process
     * @throws IllegalAccessException If a Field object has no read or write access
     * @see org.mockito.Mock
     * @since 1.3.0
     */
    private void mockMemberAnnotatedWithMock(final Object testInstance) throws IllegalAccessException {

        Class<?> currentClassType = testInstance.getClass();
        while (currentClassType != Object.class) {
            final List<Field> mockAnnotatedFieldList = Arrays.stream(currentClassType.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Mock.class))
                .toList();

            for (final Field field : mockAnnotatedFieldList) {

                final Class<?> classType = field.getType();

                // Checks if mock is applicable
                final Object currentFieldInstance = this.getValueFromField(testInstance, field);
                if (MockUtil.isMock(currentFieldInstance)) {
                    this.trackedInstanceHandlerSet.add(TrackedInstanceHandler.of(classType, currentFieldInstance));
                    continue;
                }

                // Checks if mock is not already known
                final Optional<TrackedInstanceHandler<Object>> existingMockedInstanceOptional = this.trackedInstanceHandlerSet
                    .stream()
                    .filter(element -> element.classType == classType)
                    .findFirst();
                if (existingMockedInstanceOptional.isPresent()) {
                    this.setValueToField(testInstance, field, existingMockedInstanceOptional.get().instance);
                } else {

                    // Creates mock settings from annotation
                    final Mock mockAnnotation = field.getAnnotation(Mock.class);
                    MockSettings mockSettings = Mockito.withSettings()
                        .name(mockAnnotation.name())
                        .defaultAnswer(mockAnnotation.answer());

                    if (mockAnnotation.stubOnly()) {
                        mockSettings = mockSettings.stubOnly();
                    }
                    if (mockAnnotation.serializable()) {
                        mockSettings = mockSettings.serializable();
                    }
                    if (mockAnnotation.strictness() != Mock.Strictness.TEST_LEVEL_DEFAULT) {
                        Strictness strictness = Strictness.valueOf(mockAnnotation.strictness().name());
                        mockSettings = mockSettings.strictness(strictness);
                    }
                    if (mockAnnotation.extraInterfaces().length > 0) {
                        mockSettings = mockSettings.extraInterfaces(mockAnnotation.extraInterfaces());
                    }

                    // Creates mock
                    Object mockedInstance = Mockito.mock(classType, mockSettings);
                    if (field.isAnnotationPresent(Spy.class)) {
                        mockedInstance = Mockito.spy(mockedInstance);
                    }

                    // Sets mocked value to the field
                    this.setValueToField(testInstance, field, mockedInstance);

                    // Keeps track of the mocked value (only the last instance is needed)
                    final TrackedInstanceHandler<Object> mockedInstanceHandler = TrackedInstanceHandler.of(
                        classType,
                        classType.cast(mockedInstance));
                    this.trackedInstanceHandlerSet.add(mockedInstanceHandler);
                }
            }

            // Next
            currentClassType = currentClassType.getSuperclass();
        }
    }

    /**
     * Injects all members.
     *
     * @param appInjector  The application injector instance
     * @param mockInjector The mocked classes injector instance
     * @param testInstance The instance to post-process
     * @throws IllegalAccessException If a Field object has no read or write access
     * @since 1.3.0
     */
    private void injectMembers(final Injector appInjector,
                               final Injector mockInjector,
                               final Object testInstance) throws IllegalAccessException {

        // Injects all "@InjectMocks" annotated members
        this.injectMembersAnnotatedWithInjectMocks(mockInjector, testInstance);

        // Injects all "@Inject" annotated members
        appInjector.injectMembers(testInstance);

        // Injects all "@Spy" annotated members
        this.injectMembersAnnotatedWithSpyOnly(testInstance);
    }

    /**
     * Mocks "@InjectMocks" annotated fields.
     *
     * @param mockInjector The mocked classes injector instance
     * @param testInstance The instance to post-process
     * @throws IllegalAccessException If a Field object has no read or write access
     * @see org.mockito.InjectMocks
     * @since 1.3.0
     */
    private void injectMembersAnnotatedWithInjectMocks(final Injector mockInjector,
                                                       final Object testInstance) throws IllegalAccessException {

        final Set<Field> fieldToMockSet = new HashSet<>();
        final InjectMocksScanner injectMocksScanner = new InjectMocksScanner(testInstance.getClass());
        injectMocksScanner.addTo(fieldToMockSet);

        try {
            for (final Field field : fieldToMockSet) {
                // Injects & mocks
                final Class<?> classType = field.getType();
                Object instance = mockInjector.getInstance(classType);

                if (field.isAnnotationPresent(Spy.class)) {
                    instance = Mockito.spy(instance);
                    this.trackedInstanceHandlerSet.add(TrackedInstanceHandler.of(classType, instance));
                }

                this.setValueToField(testInstance, field, instance);
            }
        } catch (final ConfigurationException exception) {
            LOGGER.error("@InjectMock only works with mocked values!");
            LOGGER.error("If you want to mixing mocked/Not mocked values, consider using @Inject directly.");
            throw exception;
        }
    }

    /**
     * Spies all "@Spy" annotated members.
     *
     * @param testInstance The instance to post-process
     * @throws IllegalAccessException If a Field object has no read or write access
     * @see org.mockito.Spy
     * @since 1.3.0
     */
    private void injectMembersAnnotatedWithSpyOnly(final Object testInstance) throws IllegalAccessException {

        Class<?> currentClassType = testInstance.getClass();
        while (currentClassType != Object.class) {
            final List<Field> spyAnnotatedFieldList = Arrays.stream(currentClassType.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Spy.class))
                .filter(field -> !field.isAnnotationPresent(Mock.class))
                .filter(field -> !field.isAnnotationPresent(InjectMocks.class))
                .toList();

            for (final Field field : spyAnnotatedFieldList) {
                // Retrieves field value
                final Object instance = this.getValueFromField(testInstance, field);

                // Checks if already a spied instance
                if (instance != null && MockUtil.isSpy(instance)) {
                    continue;
                }

                // Applies "Spy"
                final Object spiedInstance;
                final Class<?> classType;
                if (instance == null) {
                    classType = field.getType();
                    spiedInstance = Mockito.spy(classType);
                } else {
                    classType = instance.getClass();
                    spiedInstance = Mockito.spy(instance);
                }
                this.setValueToField(testInstance, field, spiedInstance);

                // Keeps track of the spied instance
                this.trackedInstanceHandlerSet.add(TrackedInstanceHandler.of(classType, spiedInstance));
            }

            // Next
            currentClassType = currentClassType.getSuperclass();
        }
    }

    /**
     * Gets value of the given field.
     *
     * @param instance The instance where are located the field
     * @param field    The field
     * @throws IllegalAccessException If Field object has no read access
     * @since 1.3.0
     */
    private Object getValueFromField(final Object instance, final Field field) throws IllegalAccessException {

        // If field is not accessible, must set it accessible
        final boolean canAccess = field.canAccess(instance);
        if (!canAccess) {
            field.setAccessible(true);
        }

        // Gets the value
        final Object value = field.get(instance);

        // Rollback accessibility (if needed)
        if (!canAccess) {
            field.setAccessible(false);
        }

        return value;
    }

    /**
     * Sets value to the given field.
     *
     * @param instance The instance where are located the field
     * @param field    The field
     * @param value    The value to assign
     * @throws IllegalAccessException If Field object has no write access
     * @since 1.3.0
     */
    private void setValueToField(final Object instance, final Field field, final Object value) throws IllegalAccessException {

        // If field is not accessible, must set it accessible
        final boolean canAccess = field.canAccess(instance);
        if (!canAccess) {
            field.setAccessible(true);
        }

        // Sets the value
        field.set(instance, value);

        // Rollback accessibility (if needed)
        if (!canAccess) {
            field.setAccessible(false);
        }
    }

    /**
     * Mocked instance handler.
     *
     * @param classType The mocked instance type
     * @param instance  The mocked instance itself
     * @param <T>       The type of the mocked instance
     * @since 1.3.0
     */
    private record TrackedInstanceHandler<T>(Class<T> classType,
                                             T instance) {

        /**
         * Creates a new instance.
         *
         * @param classType The class type
         * @param instance  The class instance
         * @return Newly created instance
         * @since 1.3.0
         */
        @SuppressWarnings("unchecked")
        public static TrackedInstanceHandler<Object> of(final Class<?> classType,
                                                        final Object instance) {

            return new TrackedInstanceHandler<>((Class<Object>) classType, instance);
        }

        @Override
        public boolean equals(final Object o) {

            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final TrackedInstanceHandler<?> that = (TrackedInstanceHandler<?>) o;
            return classType.equals(that.classType);
        }

        @Override
        public int hashCode() {

            return Objects.hash(classType);
        }
    }
}
