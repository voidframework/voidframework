package dev.voidframework.web.server;

import com.typesafe.config.Config;
import dev.voidframework.core.lang.Either;
import dev.voidframework.web.exception.HttpException;
import dev.voidframework.web.http.Context;
import dev.voidframework.web.http.Cookie;
import dev.voidframework.web.http.FormItem;
import dev.voidframework.web.http.HttpRequest;
import dev.voidframework.web.http.HttpRequestBodyContent;
import dev.voidframework.web.http.HttpRequestHandler;
import dev.voidframework.web.http.Result;
import dev.voidframework.web.http.Session;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.CookieImpl;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.server.handlers.form.MultiPartParserDefinition;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Undertow HTTP handler.
 */
public class UndertowHttpHandler implements HttpHandler {

    private static final Duration COOKIE_LANG_DURATION = Duration.ofDays(365);

    private final Config configuration;
    private final HttpRequestHandler httpRequestHandler;
    private final SessionSigner sessionSigner;
    private final MultiPartParserDefinition multiPartParserDefinition;

    /**
     * Build a new instance.
     *
     * @param configuration      The application configuration
     * @param httpRequestHandler The HTTP request handler
     * @param sessionSigner      The session signer
     */
    public UndertowHttpHandler(final Config configuration,
                               final HttpRequestHandler httpRequestHandler,
                               final SessionSigner sessionSigner) {
        this.configuration = configuration;
        this.httpRequestHandler = httpRequestHandler;
        this.sessionSigner = sessionSigner;

        this.multiPartParserDefinition = new MultiPartParserDefinition()
            //.setTempFileLocation(new File(System.getProperty("java.io.tmpdir")).toPath())
            .setTempFileLocation(null)
            .setDefaultEncoding("UTF-8");
        this.multiPartParserDefinition.setFileSizeThreshold(
            this.configuration.getMemorySize("voidframework.web.fileSizeThreshold").toBytes());
    }

    @Override
    public void handleRequest(final HttpServerExchange httpServerExchange) {
        httpServerExchange.startBlocking();

        // Create HTTP request
        final Either<HttpRequest, HttpException.BadRequest> httpRequestOrException = createHttpRequestWithOptionalBodyContent(httpServerExchange);
        final HttpRequest httpRequest;
        if (httpRequestOrException.hasLeft()) {
            httpRequest = httpRequestOrException.getLeft();
        } else {
            httpRequest = createHttpRequestWithoutBodyContent(httpServerExchange);
        }

        // Build Context
        Cookie sessionCookie = httpRequest.getCookie(this.configuration.getString("voidframework.web.session.cookieName"));
        final Session session;
        if (sessionCookie != null) {
            session = sessionSigner.verify(sessionCookie.value());
        } else {
            session = new Session();
        }

        final Locale i18nLocale;
        final List<String> availableLanguageList = this.configuration.getStringList("voidframework.web.language.availableLanguages");
        Cookie i18nCookie = httpRequest.getCookie(this.configuration.getString("voidframework.web.language.cookieName"));
        if (i18nCookie != null && availableLanguageList.contains(i18nCookie.value())) {
            i18nLocale = Locale.forLanguageTag(i18nCookie.value());
        } else {
            i18nLocale = availableLanguageList.isEmpty() ? null : Locale.forLanguageTag(availableLanguageList.get(0));
        }

        final Context context = new Context(httpRequest, session, i18nLocale);

        // Process request (if no error occur before)
        final Result result = httpRequestOrException.hasLeft()
            ? httpRequestHandler.onRouteRequest(context)
            : httpRequestHandler.onBadRequest(context, httpRequestOrException.getRight());

        // Check if exchange is still available
        if (httpServerExchange.isComplete()) {
            return;
        }

        // Set the return HttpCode and Content-Type
        httpServerExchange.setStatusCode(result.getHttpCode());
        httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, result.getContentType());

        // Headers
        for (final Map.Entry<String, String> entrySet : result.getHeaders().entrySet()) {
            httpServerExchange.getResponseHeaders().put(
                new HttpString(entrySet.getKey()),
                entrySet.getValue());
        }

        // Persist session to Cookie
        if (context.getSession().isModified()) {
            sessionCookie = Cookie.of(
                this.configuration.getString("voidframework.web.session.cookieName"),
                sessionSigner.sign(context.getSession()),
                this.configuration.getBoolean("voidframework.web.session.cookieHttpOnly"),
                this.configuration.getBoolean("voidframework.web.session.cookieSecure"),
                !context.getSession().isEmpty()
                    ? this.configuration.getDuration("voidframework.web.session.timeToLive")
                    : Duration.ZERO);

            result.withCookie(sessionCookie);
        }

        // Persist locale to Cookie
        if (context.getLocale() != null) {
            i18nCookie = Cookie.of(
                this.configuration.getString("voidframework.web.language.cookieName"),
                context.getLocale().toLanguageTag(),
                this.configuration.getBoolean("voidframework.web.language.cookieHttpOnly"),
                this.configuration.getBoolean("voidframework.web.language.cookieSecure"),
                COOKIE_LANG_DURATION);

            result.withCookie(i18nCookie);
        }

        // Cookies
        for (final Cookie cookie : result.getCookies().values()) {
            CookieImpl cookieImpl = new CookieImpl(cookie.name(), cookie.value())
                .setDomain(cookie.domain())
                .setPath(cookie.path())
                .setHttpOnly(cookie.isHttpOnly())
                .setSecure(cookie.isSecure())
                .setDiscard(cookie.timeToLive() == Duration.ZERO);
            if (cookie.timeToLive() != null) {
                cookieImpl = cookieImpl.setMaxAge((int) cookie.timeToLive().toSeconds());
            }

            httpServerExchange.setResponseCookie(cookieImpl);
        }

        // Returns content
        final OutputStream outputStream = httpServerExchange.getOutputStream();
        final InputStream inputStream = result.getInputStream();
        try {
            httpServerExchange.setResponseContentLength(inputStream.available());

            final byte[] buffer = new byte[8192];
            int readLength;
            while ((readLength = inputStream.read(buffer, 0, buffer.length)) > 0) {
                outputStream.write(buffer, 0, readLength);
                outputStream.flush();
            }
        } catch (final Exception ignore) {
        } finally {
            IOUtils.closeQuietly(outputStream);
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * Creates an HTTP request from the current exchange with body content parsing (if available).
     *
     * @param httpServerExchange The exchange to use
     * @return The newly created HTTP request, otherwise, an exception
     */
    private Either<HttpRequest, HttpException.BadRequest> createHttpRequestWithOptionalBodyContent(final HttpServerExchange httpServerExchange) {
        final HttpRequest httpRequest;

        String contentType = httpServerExchange.getRequestHeaders().getLast("Content-Type");
        if (contentType != null) {
            contentType = contentType.split(";")[0];
        }

        if (contentType != null) {

            // Try to parse content
            try (final FormDataParser formDataParser = FormParserFactory.builder(false)
                .addParser(this.multiPartParserDefinition)
                .build()
                .createParser(httpServerExchange)) {

                if (formDataParser != null) {
                    final Map<String, List<FormItem>> formItemPerKeyMap = new HashMap<>();
                    final FormData formData = formDataParser.parseBlocking();
                    for (final String formDataKey : formData) {
                        final List<FormItem> formItemList = formItemPerKeyMap.computeIfAbsent(formDataKey, k -> new ArrayList<>());
                        for (final FormData.FormValue formValue : formData.get(formDataKey)) {
                            if (formValue.isFileItem()) {
                                formItemList.add(new FormItem(null, formValue.getCharset(), formValue.isFileItem(), formValue.getFileItem().getInputStream()));
                            } else {
                                formItemList.add(new FormItem(formValue.getValue(), formValue.getCharset(), formValue.isFileItem(), null));
                            }
                        }
                    }

                    httpRequest = new UndertowRequest(
                        httpServerExchange,
                        new HttpRequestBodyContent(contentType, null, formItemPerKeyMap));

                } else {
                    final byte[] content = httpServerExchange.getInputStream().readAllBytes();
                    httpRequest = new UndertowRequest(
                        httpServerExchange,
                        new HttpRequestBodyContent(contentType, content, null));
                }
            } catch (final IOException exception) {
                return Either.ofRight(new HttpException.BadRequest("Can't parse body content", exception));
            }
        } else {
            httpRequest = createHttpRequestWithoutBodyContent(httpServerExchange);
        }

        return Either.ofLeft(httpRequest);
    }

    /**
     * Creates an HTTP request from the current exchange without body content parsing.
     *
     * @param httpServerExchange The exchange to use
     * @return The newly created HTTP request
     */
    private HttpRequest createHttpRequestWithoutBodyContent(final HttpServerExchange httpServerExchange) {
        return new UndertowRequest(httpServerExchange, new HttpRequestBodyContent(null, null, null));
    }
}
