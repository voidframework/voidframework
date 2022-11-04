package dev.voidframework.web.server.http;

import com.typesafe.config.Config;
import dev.voidframework.core.constant.StringConstants;
import dev.voidframework.core.lang.Either;
import dev.voidframework.core.utils.IOUtils;
import dev.voidframework.core.utils.JsonUtils;
import dev.voidframework.web.exception.HttpException;
import dev.voidframework.web.exception.TempFileLocationException;
import dev.voidframework.web.http.Context;
import dev.voidframework.web.http.Cookie;
import dev.voidframework.web.http.FlashMessages;
import dev.voidframework.web.http.FormItem;
import dev.voidframework.web.http.HttpHeaderNames;
import dev.voidframework.web.http.HttpRequest;
import dev.voidframework.web.http.HttpRequestBodyContent;
import dev.voidframework.web.http.Result;
import dev.voidframework.web.http.Session;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.CookieImpl;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormEncodedDataDefinition;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.server.handlers.form.MultiPartParserDefinition;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
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
    private final FormEncodedDataDefinition formEncodedDataDefinition;
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

        this.formEncodedDataDefinition = new FormEncodedDataDefinition();
        this.formEncodedDataDefinition.setDefaultEncoding("UTF-8");

        Path tempFileLocationPath = null;
        if (this.configuration.hasPath("voidframework.web.server.tempFileLocation")) {
            final String tempFileLocationString = this.configuration.getString("voidframework.web.server.tempFileLocation");
            tempFileLocationPath = Paths.get(tempFileLocationString);

            final File tempFileLocationFile = tempFileLocationPath.toFile();
            if (!tempFileLocationFile.exists() && !tempFileLocationFile.mkdirs()) {
                throw new TempFileLocationException.DirectoryCreationFailure(tempFileLocationString);
            }
        }

        this.multiPartParserDefinition = new MultiPartParserDefinition()
            .setTempFileLocation(tempFileLocationPath)
            .setDefaultEncoding("UTF-8");
        this.multiPartParserDefinition.setFileSizeThreshold(
            this.configuration.getMemorySize("voidframework.web.server.fileSizeThreshold").toBytes());
    }

    @Override
    public void handleRequest(final HttpServerExchange httpServerExchange) {

        if (httpServerExchange.isInIoThread()) {
            httpServerExchange.dispatch(this);
            return;
        }

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

        Cookie flashMessagesCookie = httpRequest.getCookie(this.configuration.getString("voidframework.web.flashMessages.cookieName"));
        final FlashMessages flashMessages;
        if (flashMessagesCookie != null) {
            flashMessages = new FlashMessages(
                JsonUtils.fromJson(JsonUtils.toJson(flashMessagesCookie.value().getBytes(StandardCharsets.UTF_8)), FlashMessages.class));
        } else {
            flashMessages = new FlashMessages();
        }

        final Locale i18nLocale;
        final List<String> availableLanguageList = this.configuration.getStringList("voidframework.web.language.availableLanguages");
        Cookie i18nCookie = httpRequest.getCookie(this.configuration.getString("voidframework.web.language.cookieName"));
        if (i18nCookie != null && availableLanguageList.contains(i18nCookie.value())) {
            i18nLocale = Locale.forLanguageTag(i18nCookie.value());
        } else {
            i18nLocale = determineI18NLocale(httpRequest, availableLanguageList);
        }

        final Context context = new Context(httpRequest, session, flashMessages, i18nLocale);

        // Process request (if no error occur before)
        final Result result = httpRequestOrException.hasLeft()
            ? httpRequestHandler.onRouteRequest(context)
            : httpRequestHandler.onBadRequest(context, httpRequestOrException.getRight());

        // Checks if exchange is still available
        if (httpServerExchange.isComplete()) {
            return;
        }

        // Sets the return HttpCode and Content-Type
        httpServerExchange.setStatusCode(result.getHttpCode());
        httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, result.getContentType());

        // Headers
        for (final Map.Entry<String, String> entrySet : result.getHeaders().entrySet()) {
            httpServerExchange.getResponseHeaders().put(
                new HttpString(entrySet.getKey()),
                entrySet.getValue());
        }

        // Persists session to Cookie
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

        // Persists flash messages to Cookie
        if (context.getFlashMessages().isModified()) {
            flashMessagesCookie = Cookie.of(
                this.configuration.getString("voidframework.web.flashMessages.cookieName"),
                JsonUtils.toJson(context.getFlashMessages()).toString(),
                this.configuration.getBoolean("voidframework.web.flashMessages.cookieHttpOnly"),
                this.configuration.getBoolean("voidframework.web.flashMessages.cookieSecure"),
                context.getFlashMessages().isEmpty() ? Duration.ZERO : null);

            result.withCookie(flashMessagesCookie);
        }

        // Persists locale to Cookie
        if (context.getLocale() != null && context.getLocale() != i18nLocale) {
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

            httpServerExchange.setResponseCookie(cookieImpl.setSameSite(true).setSameSiteMode(cookie.sameSiteMode()));
        }

        // Returns content
        final InputStream inputStream = result.getResultProcessor().getInputStream();
        if (inputStream != null) {
            final OutputStream outputStream = httpServerExchange.getOutputStream();
            try {
                httpServerExchange.setResponseContentLength(inputStream.available());

                final byte[] buffer = new byte[8192];
                int nbReadBytes;
                while ((nbReadBytes = inputStream.read(buffer, 0, buffer.length)) > 0) {
                    outputStream.write(buffer, 0, nbReadBytes);
                    outputStream.flush();
                }
            } catch (final Exception ignore) {
                // This exception is not important
            } finally {
                IOUtils.closeWithoutException(outputStream);
                IOUtils.closeWithoutException(inputStream);
            }
        }

        forceCloseUploadedFileInputStream(httpRequest);
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
            contentType = contentType.split(StringConstants.SEMICOLON)[0];
        }

        if (contentType != null) {

            // Try to parse content
            try (final FormDataParser formDataParser = FormParserFactory.builder(false)
                .addParsers(this.formEncodedDataDefinition, this.multiPartParserDefinition)
                .build()
                .createParser(httpServerExchange)) {

                final dev.voidframework.web.http.FormData formItemPerKeyMap = new dev.voidframework.web.http.FormData();
                if (formDataParser != null) {

                    final FormData formData = formDataParser.parseBlocking();
                    for (final String formDataKey : formData) {
                        final List<FormItem> formItemList = formItemPerKeyMap.computeIfAbsent(formDataKey, k -> new ArrayList<>());
                        for (final FormData.FormValue formValue : formData.get(formDataKey)) {
                            if (formValue.isFileItem()) {
                                formItemList.add(new FormItem(
                                    null,
                                    formValue.getCharset(),
                                    formValue.isFileItem(),
                                    formValue.getFileItem().getFileSize(),
                                    formValue.getFileItem().getInputStream()));
                            } else {
                                formItemList.add(new FormItem(
                                    formValue.getValue(),
                                    formValue.getCharset(),
                                    formValue.isFileItem(),
                                    -1,
                                    null));
                            }
                        }
                    }

                    httpRequest = new UndertowHttpRequest(
                        httpServerExchange,
                        new HttpRequestBodyContent(contentType, httpServerExchange.getInputStream(), formItemPerKeyMap));

                } else {
                    httpRequest = new UndertowHttpRequest(
                        httpServerExchange,
                        new HttpRequestBodyContent(contentType, httpServerExchange.getInputStream(), formItemPerKeyMap));
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

        return new UndertowHttpRequest(httpServerExchange, new HttpRequestBodyContent(null, null, null));
    }

    /**
     * Determine I18N {@code Locale} to use for the current request.
     *
     * @param httpRequest           The current HTTP request
     * @param availableLanguageList Available languages
     * @return The {@code Locale} to use
     */
    private Locale determineI18NLocale(final HttpRequest httpRequest, final List<String> availableLanguageList) {

        if (availableLanguageList.isEmpty()) {
            return null;
        }

        final String acceptLanguage = httpRequest
            .getHeader(HttpHeaderNames.ACCEPT_LANGUAGE)
            .split(StringConstants.COMMA)[0]
            .trim();

        return availableLanguageList.contains(acceptLanguage)
            ? Locale.forLanguageTag(acceptLanguage)
            : Locale.forLanguageTag(availableLanguageList.get(0));
    }

    /**
     * Force close of all upload files {@code InputStream}.
     *
     * @param httpRequest The current HTTP request
     */
    private void forceCloseUploadedFileInputStream(final HttpRequest httpRequest) {

        IOUtils.closeWithoutException(httpRequest.getBodyContent().asRaw());

        // If a FormData exists, we have to be sure that the potential streams are all closed
        if (httpRequest.getBodyContent().asFormData() != null) {
            for (final List<FormItem> formItemList : httpRequest.getBodyContent().asFormData().values()) {
                for (final FormItem formItem : formItemList) {
                    IOUtils.closeWithoutException(formItem.inputStream());
                }
            }
        }
    }
}
