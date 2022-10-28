package dev.voidframework.vfs.module;

import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import dev.voidframework.vfs.engine.VirtualFileStorage;
import dev.voidframework.vfs.exception.VirtualFileStorageException;

import java.lang.reflect.InvocationTargetException;

/**
 * Virtual File Storage provider. This provider is special, it exposes methods to manually manage
 * the {@link VirtualFileStorage} to return when a user calls the method {@link #get()}.
 */
@Singleton
public class VirtualFileStorageProvider implements Provider<VirtualFileStorage> {

    private final Class<? extends VirtualFileStorage> classType;
    private final Config vfsEngineConfiguration;
    private VirtualFileStorage currentInstance;

    /**
     * Build a new instance.
     *
     * @param classType              The VFS engine class type
     * @param vfsEngineConfiguration The VFS engine configuration
     */
    public VirtualFileStorageProvider(final Class<? extends VirtualFileStorage> classType,
                                      final Config vfsEngineConfiguration) {

        this.classType = classType;
        this.vfsEngineConfiguration = vfsEngineConfiguration;
        this.currentInstance = null;
    }

    @Override
    public VirtualFileStorage get() {

        if (this.currentInstance == null) {
            try {
                instantiateVirtualFileStorage();
            } catch (final IllegalArgumentException
                           | NoSuchMethodException
                           | InstantiationException
                           | IllegalAccessException
                           | InvocationTargetException ignore) {

                throw new VirtualFileStorageException.CantInstantiateEngine();
            }
        }

        return this.currentInstance;
    }

    /**
     * Instantiates the VFS engine.
     *
     * @throws NoSuchMethodException     If a matching method is not found
     * @throws InvocationTargetException If the underlying constructor throws an exception
     * @throws InstantiationException    If the class that declares the underlying constructor represents an abstract class
     * @throws IllegalAccessException    If this Constructor object is enforcing Java language access control and the underlying constructor is inaccessible
     */
    private void instantiateVirtualFileStorage()
        throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        try {
            this.currentInstance = classType.getDeclaredConstructor().newInstance();
        } catch (final IllegalArgumentException
                       | NoSuchMethodException
                       | InstantiationException
                       | IllegalAccessException
                       | InvocationTargetException ignore) {

            this.currentInstance = classType.getDeclaredConstructor(Config.class).newInstance(this.vfsEngineConfiguration);
        }
    }
}
