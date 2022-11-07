package dev.voidframework.web.server.http;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.core.utils.ReflectionUtils;
import dev.voidframework.web.http.HttpHeaderNames;
import dev.voidframework.web.http.HttpRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;

import java.util.List;
import java.util.Locale;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class UndertowHttpHandlerTest {

    @Test
    void determineI18NLocaleProvidedSimpleValue() {

        // Arrange
        final Config configuration = ConfigFactory.parseString("voidframework.web.server.fileSizeThreshold = 256 KiB");
        final UndertowHttpHandler undertowHttpHandler = new UndertowHttpHandler(configuration, null, null);
        final HttpRequest httpRequest = Mockito.mock(HttpRequest.class);

        Mockito.when(httpRequest.getHeader(HttpHeaderNames.ACCEPT_LANGUAGE)).thenReturn("fr,en-GB;q=0.9,en;q=0.8,la;q=0.7,ro;q=0.6");

        // Act
        final Locale locale = ReflectionUtils.callMethod(undertowHttpHandler,
            "determineI18NLocale",
            Locale.class,
            new Class[]{HttpRequest.class, List.class},
            httpRequest,
            List.of("en", "fr"));

        // Assert
        Assertions.assertEquals(Locale.forLanguageTag("fr"), locale);
    }

    @Test
    void determineI18NLocaleNotProvided() {

        // Arrange
        final Config configuration = ConfigFactory.parseString("voidframework.web.server.fileSizeThreshold = 256 KiB");
        final UndertowHttpHandler undertowHttpHandler = new UndertowHttpHandler(configuration, null, null);
        final HttpRequest httpRequest = Mockito.mock(HttpRequest.class);

        Mockito.when(httpRequest.getHeader(HttpHeaderNames.ACCEPT_LANGUAGE)).thenReturn(null);

        // Act
        final Locale locale = ReflectionUtils.callMethod(undertowHttpHandler,
            "determineI18NLocale",
            Locale.class,
            new Class[]{HttpRequest.class, List.class},
            httpRequest,
            List.of("en", "fr"));

        // Assert
        Assertions.assertEquals(Locale.forLanguageTag("en"), locale);
    }

    @Test
    void determineI18NLocaleLanguageListNotConfigured() {

        // Arrange
        final Config configuration = ConfigFactory.parseString("voidframework.web.server.fileSizeThreshold = 256 KiB");
        final UndertowHttpHandler undertowHttpHandler = new UndertowHttpHandler(configuration, null, null);
        final HttpRequest httpRequest = Mockito.mock(HttpRequest.class);

        Mockito.when(httpRequest.getHeader(HttpHeaderNames.ACCEPT_LANGUAGE)).thenReturn(null);

        // Act
        final Locale locale = ReflectionUtils.callMethod(undertowHttpHandler,
            "determineI18NLocale",
            Locale.class,
            new Class[]{HttpRequest.class, List.class},
            httpRequest,
            List.of());

        // Assert
        Assertions.assertNull(locale);
    }
}
