package dev.voidframework.restclient.module;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.core.classestoload.ScannedClassesToLoad;
import dev.voidframework.restclient.annotation.RestClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

import java.util.List;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class RestClientTest {

    @Test
    void getWithQueryVariable() {

        // Arrange
        final EchoApiRestClient echoApiRestClient = this.createRestClientProxy();

        // Act
        final String body = echoApiRestClient.getWithQueryVariable("Hello World!!");

        // Assert
        Assertions.assertNotNull(body);
        Assertions.assertTrue(body.contains("Hello World!!"));
    }

    @Test
    void getWithCustomHeader() {

        // Arrange
        final EchoApiRestClient echoApiRestClient = this.createRestClientProxy();

        // Act
        final String body = echoApiRestClient.getWithCustomHeader();

        // Assert
        Assertions.assertNotNull(body);
        Assertions.assertTrue(body.contains("mycustomheader"));
        Assertions.assertTrue(body.contains("1234567"));
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
        String getWithQueryVariable(@Query("msg") final String message);

        @GET("/get")
        @Headers({"mycustomheader: 1234567"})
        String getWithCustomHeader();
    }
}
