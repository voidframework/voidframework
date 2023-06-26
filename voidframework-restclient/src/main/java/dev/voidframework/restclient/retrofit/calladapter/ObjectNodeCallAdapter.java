package dev.voidframework.restclient.retrofit.calladapter;

import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.voidframework.restclient.exception.RestClientException;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Adapts a Call with response of type ObjectNode into a ObjectNode.
 *
 * @since 1.9.0
 */
public final class ObjectNodeCallAdapter implements CallAdapter<ObjectNode, ObjectNode> {

    @Override
    public Type responseType() {

        return ObjectNode.class;
    }

    @Override
    public ObjectNode adapt(final Call<ObjectNode> call) {

        try {
            final Response<ObjectNode> response = call.execute();
            return response.body();
        } catch (final IOException exception) {
            throw new RestClientException.CallAdapterProcessingException(exception);
        }
    }
}
