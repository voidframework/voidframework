package dev.voidframework.restclient.retrofit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.voidframework.restclient.retrofit.calladapter.ArrayNodeCallAdapter;
import dev.voidframework.restclient.retrofit.calladapter.GenericCallAdapter;
import dev.voidframework.restclient.retrofit.calladapter.JsonNodeCallAdapter;
import dev.voidframework.restclient.retrofit.calladapter.ObjectNodeCallAdapter;
import dev.voidframework.restclient.retrofit.calladapter.StringCallAdapter;
import retrofit2.Call;
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
        } else if (type == JsonNode.class) {
            return new JsonNodeCallAdapter();
        } else if (type == ArrayNode.class) {
            return new ArrayNodeCallAdapter();
        } else if (type == ObjectNode.class) {
            return new ObjectNodeCallAdapter();
        } else if (type != Call.class) {
            return new GenericCallAdapter(type);
        }

        return null;
    }
}
