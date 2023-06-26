package dev.voidframework.restclient.retrofit.calladapter;

import dev.voidframework.restclient.exception.RestClientException;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Adapts a Call with response of type String into a String.
 *
 * @since 1.9.0
 */
public final class StringCallAdapter implements CallAdapter<String, String> {

    @Override
    public Type responseType() {

        return String.class;
    }

    @Override
    public String adapt(final Call<String> call) {

        try {
            final Response<String> response = call.execute();
            return response.body();
        } catch (final IOException exception) {
            throw new RestClientException.CallAdapterProcessingException(exception);
        }
    }
}
