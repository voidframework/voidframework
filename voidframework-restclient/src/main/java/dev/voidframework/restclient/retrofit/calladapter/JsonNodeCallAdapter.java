package dev.voidframework.restclient.retrofit.calladapter;

import com.fasterxml.jackson.databind.JsonNode;
import dev.voidframework.restclient.exception.RestClientException;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Adapts a Call with response of type JsonNode into a JsonNode.
 *
 * @since 1.9.0
 */
public final class JsonNodeCallAdapter implements CallAdapter<JsonNode, JsonNode> {

    @Override
    public Type responseType() {

        return JsonNode.class;
    }

    @Override
    public JsonNode adapt(final Call<JsonNode> call) {

        try {
            final Response<JsonNode> response = call.execute();
            return response.body();
        } catch (final IOException exception) {
            throw new RestClientException.CallAdapterProcessingException(exception);
        }
    }
}
