package dev.voidframework.web.http.routing;

import dev.voidframework.core.constant.CharConstants;
import dev.voidframework.core.constant.StringConstants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A single route URL.
 *
 * @param url The route URL
 * @since 1.4.0
 */
public record RouteURL(String url) {

    private static final Pattern PATTERN_EXTRACT_SIMPLIFIED_PATH_VARIABLES = Pattern.compile("\\{([a-zA-Z][a-zA-Z0-9]+)}");

    /**
     * Build a new instance.
     *
     * @param url The route URL
     * @since 1.13.0
     */
    public RouteURL {

        url = replaceSimplifiedVariable(url);
    }

    /**
     * Creates a new Route URL.
     *
     * @param route The route to use
     * @return The route URL
     * @since 1.4.0
     */
    public static RouteURL of(final String route) {

        final String cleanedRoute = cleanRoutePath(route);
        return new RouteURL(cleanedRoute);
    }

    /**
     * Creates a new Route URL.
     *
     * @param contextPath The context path
     * @param prefix      The prefix to prepend
     * @param route       The route to use
     * @return The route URL
     * @since 1.4.0
     */
    public static RouteURL of(final String contextPath, final String prefix, final String route) {

        final String cleanedContextPath = cleanContextPath(contextPath);
        final String cleanedPrefix = cleanRoutePath(prefix);
        final String cleanedRoute = cleanRoutePath(route);

        if (cleanedPrefix.endsWith(StringConstants.SLASH) && cleanedRoute.charAt(0) == CharConstants.SLASH) {
            return new RouteURL(
                cleanRoutePath(cleanedContextPath + cleanedPrefix + cleanedRoute.substring(1)));
        }

        return new RouteURL(
            cleanRoutePath(cleanedContextPath + cleanedPrefix + cleanedRoute));
    }

    /**
     * Cleans the given context path.
     *
     * @param contextPath The context path to clean
     * @return Cleaned context path
     * @since 1.4.0
     */
    private static String cleanContextPath(final String contextPath) {

        if (contextPath == null) {
            return StringConstants.EMPTY;
        }

        String cleanedContextPath = contextPath.trim();
        if (cleanedContextPath.isEmpty() || cleanedContextPath.equals(StringConstants.SLASH)) {
            return StringConstants.EMPTY;
        }

        if (cleanedContextPath.charAt(0) != CharConstants.SLASH) {
            cleanedContextPath = StringConstants.SLASH + cleanedContextPath;
        }
        if (cleanedContextPath.endsWith(StringConstants.SLASH)) {
            cleanedContextPath = cleanedContextPath.substring(0, cleanedContextPath.length() - 1);
        }

        return cleanedContextPath;
    }

    /**
     * Cleans the given route path.
     *
     * @param routePath The route path to clean
     * @return Cleaned route path
     * @since 1.4.0
     */
    private static String cleanRoutePath(final String routePath) {

        if (routePath == null) {
            return StringConstants.EMPTY;
        }

        String cleanedRoutePath = routePath.trim();
        if (cleanedRoutePath.isEmpty() || cleanedRoutePath.equals(StringConstants.SLASH)) {
            return StringConstants.SLASH;
        }

        if (cleanedRoutePath.charAt(0) != CharConstants.SLASH) {
            cleanedRoutePath = StringConstants.SLASH + cleanedRoutePath;
        }
        if (cleanedRoutePath.endsWith(StringConstants.SLASH)) {
            cleanedRoutePath = cleanedRoutePath.substring(0, cleanedRoutePath.length() - 1);
        }

        return cleanedRoutePath;
    }

    /**
     * Replace all simplified variable "{varname}" with a regular expression.
     *
     * @param url URL to transform
     * @return URL with all simplified variables replaced
     * @since 1.13.0
     */
    private String replaceSimplifiedVariable(final String url) {

        final Matcher simplifiedPathVarMatcher = PATTERN_EXTRACT_SIMPLIFIED_PATH_VARIABLES.matcher(url);
        if (simplifiedPathVarMatcher.find()) {
            return simplifiedPathVarMatcher.replaceAll("(?<$1>(.*))"); // "number example input 6"
        }

        return url;
    }

    @Override
    public String toString() {

        return this.url;
    }
}
