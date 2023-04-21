package dev.voidframework.restclient.module;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.core.classestoload.ScannedClassesToLoad;
import dev.voidframework.core.utils.JsonUtils;
import dev.voidframework.restclient.annotation.RestClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.nio.charset.StandardCharsets;
import java.util.List;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class RestClientTest {

    @Test
    void customHeader() {

        // Arrange
        final EchoApiRestClient echoApiRestClient = this.createRestClientProxy();

        // Act
        final String body = echoApiRestClient.getWithCustomHeader();

        // Assert
        Assertions.assertNotNull(body);
        Assertions.assertTrue(body.contains("mycustomheader"));
        Assertions.assertTrue(body.contains("1234567"));
    }

    @Test
    void getText() {

        // Arrange
        final EchoApiRestClient echoApiRestClient = this.createRestClientProxy();

        // Act
        final String body = echoApiRestClient.getText("Hello World!!");

        // Assert
        Assertions.assertNotNull(body);
        Assertions.assertTrue(body.contains("Hello World!!"));
    }

    @Test
    void postJsonNode() {

        // Arrange
        final EchoApiRestClient echoApiRestClient = this.createRestClientProxy();
        final JsonNode jsonNode = JsonUtils.toJson("{\"key\": \"Hello World\"}".getBytes(StandardCharsets.UTF_8));

        // Act
        final JsonNode wrappedBody = echoApiRestClient.postJsonNode(jsonNode);
        final JsonNode body = wrappedBody.get("data");

        // Assert
        Assertions.assertNotNull(body);
        Assertions.assertEquals("Hello World", body.get("key").asText());
    }

    @Test
    void postJsonNodeToDTO() {

        // Arrange
        final EchoApiRestClient echoApiRestClient = this.createRestClientProxy();
        final JsonNode jsonNode = JsonUtils.toJson("{\"key\": \"Hello World\"}".getBytes(StandardCharsets.UTF_8));

        // Act
        final SampleDTO dto = echoApiRestClient.postJsonNodeToDTO(jsonNode);

        // Assert
        Assertions.assertNotNull(dto);
        Assertions.assertNotNull(dto.data);
        Assertions.assertEquals("Hello World", dto.data.key);
    }

    @Test
    void postObjectNode() {

        // Arrange
        final EchoApiRestClient echoApiRestClient = this.createRestClientProxy();
        final JsonNode jsonNode = JsonUtils.toJson("{\"key\": \"Hello World\"}".getBytes(StandardCharsets.UTF_8));

        // Act
        final ObjectNode wrappedBody = echoApiRestClient.postObjectNode(jsonNode);
        final ObjectNode body = (ObjectNode) wrappedBody.get("data");

        // Assert
        Assertions.assertNotNull(body);
        Assertions.assertEquals("Hello World", body.get("key").asText());
    }

    /**
     * Creates "Echo API" REST Client instance.
     *
     * @return The newly created "Echo API" REST Client instance
     */
    private EchoApiRestClient createRestClientProxy() {

        final Config configuration = ConfigFactory.parseString("""
            voidframework.core.runInDevMode = true
            voidframework.restclient.maxIdleConnections = 1
            voidframework.restclient.keepAliveDuration = "30 seconds"
            voidframework.restclient.connectionTimeout = "15 seconds"
            voidframework.restclient.readTimeout = "35 seconds"
            voidframework.restclient.services.echo-api.endpoint = "https://postman-echo.com"
            """);

        final ScannedClassesToLoad scannedClassesToLoad = new ScannedClassesToLoad(
            null,
            null,
            List.of(EchoApiRestClient.class),
            null,
            null,
            null);

        final Injector injector = Guice.createInjector(Stage.PRODUCTION, new AbstractModule() {
            @Override
            protected void configure() {
                bind(Config.class).toInstance(configuration);
                install(new RestClientModule(configuration, scannedClassesToLoad));
            }
        });

        return injector.getInstance(EchoApiRestClient.class);
    }

    @RestClient("echo-api")
    private interface EchoApiRestClient {

        @GET("/get")
        String getText(@Query("msg") final String message);

        @POST("/post")
        @Headers("Content-Type: application/json")
        JsonNode postJsonNode(@Body final JsonNode message);

        @POST("/post")
        @Headers("Content-Type: application/json")
        SampleDTO postJsonNodeToDTO(@Body final JsonNode message);

        @POST("/post")
        @Headers("Content-Type: application/json")
        ObjectNode postObjectNode(@Body final JsonNode message);

        @GET("/get")
        @Headers({"mycustomheader: 1234567"})
        String getWithCustomHeader();
    }

    public static class SampleDTO {

        public final Data data;

        @JsonCreator
        public SampleDTO(@JsonProperty("data") final Data data) {

            this.data = data;
        }

        public static class Data {

            public final String key;

            @JsonCreator
            public Data(@JsonProperty("key") final String key) {

                this.key = key;
            }
        }
    }
}
