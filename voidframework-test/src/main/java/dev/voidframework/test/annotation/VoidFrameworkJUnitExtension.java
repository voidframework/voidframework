package dev.voidframework.test.annotation;

import com.google.inject.Injector;
import com.google.inject.Module;
import dev.voidframework.core.VoidApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;

/**
 * JUnit's extension to provide Void Framework context.
 */
public class VoidFrameworkJUnitExtension implements TestInstancePostProcessor {

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create("dev", "voidframework", "junit5");

    @Override
    public void postProcessTestInstance(final Object testInstance,
                                        final ExtensionContext context) throws Exception {

        // Retrieve application injector
        Injector injector = getOrCreateInjector(context);
        Assertions.assertNotNull(injector);

        // Add extra modules (for the current test)
        final ExtraGuiceModule extraGuiceModule = testInstance.getClass().getAnnotation(ExtraGuiceModule.class);
        if (extraGuiceModule != null) {
            final List<Module> moduleList = new ArrayList<>();
            for (final Class<? extends Module> moduleClassType : extraGuiceModule.value()) {
                final Module module = moduleClassType.getConstructor().newInstance();
                moduleList.add(module);
            }

            injector = injector.createChildInjector(moduleList);
        }

        // Inject all annotated member
        injector.injectMembers(testInstance);
    }

    /**
     * Retrieves or creates a new Void Framework application / Injector.
     *
     * @param context The JUnit extension context
     * @return The injector
     */
    private Injector getOrCreateInjector(final ExtensionContext context) {

        if (context.getElement().isEmpty()) {
            return null;
        }

        // Retrieve store
        final AnnotatedElement element = context.getElement().get();
        final ExtensionContext.Store store = context.getStore(NAMESPACE);

        // Get or create injector
        Injector injector = store.get(element, Injector.class);
        if (injector == null) {
            final VoidApplication voidApplication = new VoidApplication();
            voidApplication.launch();

            injector = voidApplication.getInstance(Injector.class);
            store.put(NAMESPACE, injector);
        }

        return injector;
    }
}
