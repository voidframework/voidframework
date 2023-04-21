package dev.voidframework.restclient.retrofit;

import dev.voidframework.restclient.retrofit.calladapter.StringCallAdapter;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Creates CallAdapter instances based on the return type of the service interface methods.
 *
 * @since 1.9.0
 */
public final class CallAdapterFactory extends CallAdapter.Factory {

    @Nullable
    @Override
    public CallAdapter<?, ?> get(final Type type, final Annotation[] annotations, final Retrofit retrofit) {

        if (type == String.class) {
            return new StringCallAdapter();
        }

        return null;
    }
}
