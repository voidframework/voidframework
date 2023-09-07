package dev.voidframework.restclient.module;

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
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class RestClientTest {

    static Stream<Arguments> authenticationBasicArguments() {
        return Stream.of(
            Arguments.of(Named.of("UTF-8 encoding", false), "Basic dXNlcm5hbWU6ZCE4w6cvNkg0MmrDqXEkcg=="),
            Arguments.of(Named.of("ISO-8859-1 encoding", true), "Basic dXNlcm5hbWU6ZCE45y82SDQyaulxJHI="));
    }

    @Test
    void authenticationApiKeyAddToCookie() {

        // Arrange
        final EchoApiRestClient echoApiRestClient = this.createRestClientProxy("""
            voidframework.restclient.authentication.type = "API_KEY"
            voidframework.restclient.authentication.apiKeyName = "key"
            voidframework.restclient.authentication.apiKeyValue = "value"
            voidframework.restclient.authentication.apiKeyAddTo = "COOKIE"
            """);
        final JsonNode jsonNode = JsonUtils.toJson("{\"msg\": \"Hello World\"}".getBytes(StandardCharsets.UTF_8));

        // Act
        final ObjectNode wrappedBody = echoApiRestClient.postObjectNode(jsonNode);
        final ObjectNode headers = (ObjectNode) wrappedBody.get("headers");

        // Assert
        Assertions.assertNotNull(headers);
        Assertions.assertTrue(headers.has("cookie"));
        Assertions.assertEquals("key=value", headers.get("cookie").asText());
    }

    @Test
    void authenticationApiKeyAddToHeader() {

        // Arrange
        final EchoApiRestClient echoApiRestClient = this.createRestClientProxy("""
            voidframework.restclient.authentication.type = "API_KEY"
            voidframework.restclient.authentication.apiKeyName = "key"
            voidframework.restclient.authentication.apiKeyValue = "value"
            voidframework.restclient.authentication.apiKeyAddTo = "HEADER"
            """);
        final JsonNode jsonNode = JsonUtils.toJson("{\"msg\": \"Hello World\"}".getBytes(StandardCharsets.UTF_8));

        // Act
        final ObjectNode wrappedBody = echoApiRestClient.postObjectNode(jsonNode);
        final ObjectNode headers = (ObjectNode) wrappedBody.get("headers");

        // Assert
        Assertions.assertNotNull(headers);
        Assertions.assertTrue(headers.has("key"));
        Assertions.assertEquals("value", headers.get("key").asText());
    }

    @Test
    void authenticationApiKeyAddToQueryParameter() {

        // Arrange
        final EchoApiRestClient echoApiRestClient = this.createRestClientProxy("""
            voidframework.restclient.authentication.type = "API_KEY"
            voidframework.restclient.authentication.apiKeyName = "key"
            voidframework.restclient.authentication.apiKeyValue = "value"
            voidframework.restclient.authentication.apiKeyAddTo = "QUERY_PARAMETER"
            """);
        final JsonNode jsonNode = JsonUtils.toJson("{\"msg\": \"Hello World\"}".getBytes(StandardCharsets.UTF_8));

        // Act
        final ObjectNode wrappedBody = echoApiRestClient.postObjectNode(jsonNode);
        final ObjectNode args = (ObjectNode) wrappedBody.get("args");

        // Assert
        Assertions.assertNotNull(args);
        Assertions.assertTrue(args.has("key"));
        Assertions.assertEquals("value", args.get("key").asText());
    }

    @ParameterizedTest
    @MethodSource("authenticationBasicArguments")
    void authenticationBasic(final boolean basicUseISO88591Encoding, final String expectedValue) {

        // Arrange
        final EchoApiRestClient echoApiRestClient = this.createRestClientProxy("""
            voidframework.restclient.authentication.type = "BASIC"
            voidframework.restclient.authentication.basicUsername = "username"
            voidframework.restclient.authentication.basicPassword = "d!8ç/6H42jéq$r"
            voidframework.restclient.authentication.basicUseISO88591Encoding = %s
            """.formatted(basicUseISO88591Encoding));
        final JsonNode jsonNode = JsonUtils.toJson("{\"msg\": \"Hello World\"}".getBytes(StandardCharsets.UTF_8));

        // Act
        final ObjectNode wrappedBody = echoApiRestClient.postObjectNode(jsonNode);
        final ObjectNode headers = (ObjectNode) wrappedBody.get("headers");

        // Assert
        Assertions.assertNotNull(headers);
        Assertions.assertEquals(expectedValue, headers.get("authorization").asText());
    }

    @Test
    void authenticationBearer() {

        // Arrange
        final EchoApiRestClient echoApiRestClient = this.createRestClientProxy("""
            voidframework.restclient.authentication.type = "BEARER"
            voidframework.restclient.authentication.bearerPrefix = "CustomBearerPrefix"
            voidframework.restclient.authentication.bearerToken = "e05bb007-7b88-49c1-992e-0666b01d900e"
            """);
        final JsonNode jsonNode = JsonUtils.toJson("{\"msg\": \"Hello World\"}".getBytes(StandardCharsets.UTF_8));

        // Act
        final ObjectNode wrappedBody = echoApiRestClient.postObjectNode(jsonNode);
        final ObjectNode headers = (ObjectNode) wrappedBody.get("headers");

        // Assert
        Assertions.assertNotNull(headers);
        Assertions.assertEquals("CustomBearerPrefix e05bb007-7b88-49c1-992e-0666b01d900e", headers.get("authorization").asText());
    }

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
        final JsonNode jsonNode = JsonUtils.toJson("{\"msg\": \"Hello World\"}".getBytes(StandardCharsets.UTF_8));

        // Act
        final ObjectNode wrappedBody = echoApiRestClient.postObjectNode(jsonNode);
        final ObjectNode body = (ObjectNode) wrappedBody.get("data");

        // Assert
        Assertions.assertNotNull(body);
        Assertions.assertEquals("Hello World", body.get("msg").asText());
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

        return createRestClientProxy(configuration);
    }

    /**
     * Creates "Echo API" REST Client instance.
     *
     * @param extraConfiguration Extra configuration
     * @return The newly created "Echo API" REST Client instance
     */
    private EchoApiRestClient createRestClientProxy(final String extraConfiguration) {

        final Config configuration = ConfigFactory.parseString("""
            voidframework.core.runInDevMode = true
            voidframework.restclient.maxIdleConnections = 1
            voidframework.restclient.keepAliveDuration = "30 seconds"
            voidframework.restclient.connectionTimeout = "15 seconds"
            voidframework.restclient.readTimeout = "35 seconds"
            voidframework.restclient.services.echo-api.endpoint = "https://postman-echo.com"

            %s
            """.formatted(extraConfiguration));

        return createRestClientProxy(configuration);
    }

    /**
     * Creates "Echo API" REST Client instance.
     *
     * @param configuration The application configuration
     * @return The newly created "Echo API" REST Client instance
     */
    private EchoApiRestClient createRestClientProxy(final Config configuration) {

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

    public record SampleDTO(@JsonProperty("data") RestClientTest.SampleDTO.Data data) {

        public record Data(@JsonProperty("key") String key) {
        }
    }
}
