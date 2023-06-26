package dev.voidframework.restclient.retrofit.calladapter;

import dev.voidframework.core.utils.JsonUtils;
import dev.voidframework.restclient.exception.RestClientException;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Adapts a Call with response of type Object into a Object.
 *
 * @since 1.9.0
 */
public final class GenericCallAdapter implements CallAdapter<Map<?, ?>, Object> {

    private final Type outputType;

    /**
     * Build a new instance.
     *
     * @param outputType The output type
     * @since 1.9.0
     */
    public GenericCallAdapter(final Type outputType) {

        this.outputType = outputType;
    }

    @Override
    public Type responseType() {

        return Object.class;
    }

    @Override
    public Object adapt(final Call<Map<?, ?>> call) {

        try {
            final Response<Map<?, ?>> response = call.execute();
            return JsonUtils.fromMap(response.body(), JsonUtils.objectMapper().constructType(outputType));
        } catch (final IOException exception) {
            throw new RestClientException.CallAdapterProcessingException(exception);
        }
    }
}
