package dev.voidframework.web.http.controller;

import com.google.inject.Inject;
import dev.voidframework.core.constant.StringConstants;
import dev.voidframework.i18n.Internationalization;
import dev.voidframework.web.exception.HttpException;
import dev.voidframework.web.http.HttpContentTypes;
import dev.voidframework.web.http.HttpHeaderNames;
import dev.voidframework.web.http.HttpMethod;
import dev.voidframework.web.http.Result;
import dev.voidframework.web.http.annotation.NoCSRF;
import dev.voidframework.web.http.annotation.RequestPath;
import dev.voidframework.web.http.annotation.RequestRoute;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstraction to facilitate the use "Internationalization" in JavaScript (client side).
 *
 * @since 1.5.0
 */
public abstract class AbstractJavaScriptInternationalizationController {

    private static final String JAVASCRIPT_MAP_SEPARATOR = "': '";
    private static final String ESCAPED_SIMPLE_QUOTE = "\\'";
    private static final String JAVASCRIPT_CONTENT = """
        let i18n = new function () {

            const _privateMessageMap = {
                %s
            };

            this.getMessage = function (key) {

                let msg = _privateMessageMap[key];
                if (msg === undefined) {
                    return '%%' + key + '%%';
                }

                return msg;
            };

            this.getMessage = function (key, ...arguments) {

                let msg = _privateMessageMap[key];
                if (msg === undefined) {
                    return '%%' + key + '%%';
                }

                for (let idx = 0; idx < arguments.length; idx += 1) {
                    msg = msg.replace('{' + idx + '}', arguments[idx]);
                }

                return msg;
            };
        };""";
    protected final List<String> filterKeyPatternList;
    private final Internationalization internationalization;
    private final Map<Locale, String> jsFileContentCache;

    /**
     * Build a new instance;
     *
     * @param internationalization The internationalization instance
     * @since 1.5.0
     */
    @Inject
    protected AbstractJavaScriptInternationalizationController(final Internationalization internationalization) {

        this.internationalization = internationalization;
        this.jsFileContentCache = new ConcurrentHashMap<>();
        this.filterKeyPatternList = new ArrayList<>();
    }

    /**
     * Retrieves JS I18N script.
     *
     * @param locale Locale for which retrieve messages
     * @return A result containing JavaScript code
     * @throws HttpException.NotFound If requested asset does not exist
     * @since 1.5.1
     */
    @NoCSRF
    @RequestRoute(method = HttpMethod.GET, route = "/js/messages-(?<locale>[a-zA-Z_\\-]{2,6}).js", name = "js_i18n")
    @SuppressWarnings("unused")
    public Result jsInternationalizationScript(@RequestPath("locale") final Locale locale) {

        if (locale == null) {
            throw new HttpException.NotFound();
        }

        String jsFileContent = this.jsFileContentCache.get(locale);
        if (jsFileContent == null) {
            jsFileContent = this.generateJavaScriptFile(locale);
            this.jsFileContentCache.put(locale, jsFileContent);
        }

        return Result
            .ok(jsFileContent, HttpContentTypes.APPLICATION_JAVASCRIPT, StandardCharsets.UTF_8)
            .withHeader(HttpHeaderNames.CACHE_CONTROL, "public, max-age=3600;");
    }

    /**
     * Generates JavaScript file for given locale.
     *
     * @param locale The locale for which generate JavaScript file
     * @return A String containing JavaScript content
     * @since 1.5.0
     */
    private String generateJavaScriptFile(final Locale locale) {

        final Map<String, String> messagePerKeyMap = this.internationalization.getAllMessages(locale);
        final StringBuilder stringBuilder = new StringBuilder();

        for (final Map.Entry<String, String> entry : messagePerKeyMap.entrySet()) {

            if (this.canUseKey(entry.getKey())) {
                stringBuilder
                    .append(StringConstants.SIMPLE_QUOTE)
                    .append(entry.getKey().replace(StringConstants.SIMPLE_QUOTE, ESCAPED_SIMPLE_QUOTE))
                    .append(JAVASCRIPT_MAP_SEPARATOR)
                    .append(entry.getValue())
                    .append(StringConstants.SIMPLE_QUOTE)
                    .append(StringConstants.COMMA)
                    .append(StringConstants.LINE_FEED);
            }
        }

        return JAVASCRIPT_CONTENT.formatted(stringBuilder.toString());
    }

    /**
     * Checks if the key can be used.
     *
     * @param key The key
     * @return {@code true} if the key can be user, otherwise, {@code false}
     * @since 1.5.0
     */
    private boolean canUseKey(final String key) {

        if (this.filterKeyPatternList.isEmpty()) {
            return true;
        }

        for (final String pattern : this.filterKeyPatternList) {
            if (key.matches(pattern)) {
                return true;
            }
        }

        return false;
    }
}
