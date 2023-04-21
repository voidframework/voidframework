package dev.voidframework.restclient.retrofit.calladapter;

import com.fasterxml.jackson.databind.node.ArrayNode;
import dev.voidframework.restclient.exception.RestClientException;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Adapts a Call with response of type ArrayNode into a ArrayNode.
 *
 * @since 1.9.0
 */
public final class ArrayNodeCallAdapter implements CallAdapter<ArrayNode, ArrayNode> {

    @Override
    public Type responseType() {

        return ArrayNode.class;
    }

    @Override
    public ArrayNode adapt(final Call<ArrayNode> call) {

        try {
            final Response<ArrayNode> response = call.execute();
            return response.body();
        } catch (final IOException exception) {
            throw new RestClientException.CallAdapterProcessingException(exception);
        }
    }
}
