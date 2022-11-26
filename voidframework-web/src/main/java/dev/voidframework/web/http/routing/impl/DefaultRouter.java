package dev.voidframework.web.http.routing.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import dev.voidframework.core.constant.StringConstants;
import dev.voidframework.core.utils.ProxyDetectorUtils;
import dev.voidframework.web.exception.RoutingException;
import dev.voidframework.web.http.HttpMethod;
import dev.voidframework.web.http.annotation.WithFilter;
import dev.voidframework.web.http.filter.Filter;
import dev.voidframework.web.http.routing.ResolvedRoute;
import dev.voidframework.web.http.routing.Route;
import dev.voidframework.web.http.routing.RouteURL;
import dev.voidframework.web.http.routing.Router;
import dev.voidframework.web.http.routing.RouterPostInitialization;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Default implementation of {@link Router}.
 */
public class DefaultRouter implements Router, RouterPostInitialization {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRouter.class);
    private static final Pattern PATTERN_EXTRACT_REGEXP_GROUP = Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z\\d]*)>.*\\)");
    private static final Pattern PATTERN_EXTRACT_REGEXP_GROUP_NAME = Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z\\d]*)>");

    private final Map<HttpMethod, List<Route>> routeListPerHttpMethodMap;
    private final Map<String, List<Route>> routeListPerNameMap;

    /**
     * Build a new instance.
     */
    public DefaultRouter() {

        this.routeListPerHttpMethodMap = new EnumMap<>(HttpMethod.class);
        this.routeListPerNameMap = new HashMap<>();
    }

    private static Set<String> getNamedGroup(final String regex) {

        final Set<String> namedGroups = new TreeSet<>();

        final Matcher matcher = PATTERN_EXTRACT_REGEXP_GROUP_NAME.matcher(regex);
        while (matcher.find()) {
            namedGroups.add(matcher.group(1));
        }

        return namedGroups;
    }

    @Override
    public void addRoute(final HttpMethod httpMethod,
                         final RouteURL routeURL,
                         final Class<?> controllerClassType,
                         final Method method) {

        this.addRoute(httpMethod, routeURL, controllerClassType, method, StringUtils.EMPTY);
    }

    @Override
    public void addRoute(final HttpMethod httpMethod,
                         final RouteURL routeURL,
                         final Class<?> controllerClassType,
                         final Method method,
                         final String name) {

        this.checkAddRouteArguments(httpMethod, routeURL, controllerClassType, method);

        final Class<?> controllerClass = ProxyDetectorUtils.isProxy(controllerClassType)
            ? controllerClassType.getSuperclass()
            : controllerClassType;
        LOGGER.debug("Add route {} {} {}::{}", httpMethod, routeURL, controllerClass.getName(), method.getName());

        final List<Class<? extends Filter>> filterClassList = new ArrayList<>();
        WithFilter withFilter = controllerClass.getAnnotation(WithFilter.class);
        if (withFilter != null) {
            filterClassList.addAll(Arrays.asList(withFilter.value()));
        }

        withFilter = method.getAnnotation(WithFilter.class);
        if (withFilter != null) {
            filterClassList.addAll(Arrays.asList(withFilter.value()));
        }

        final Route route = new Route(httpMethod, Pattern.compile(routeURL.url()), filterClassList, controllerClass, method);
        this.routeListPerHttpMethodMap.computeIfAbsent(httpMethod, (key) -> new ArrayList<>()).add(route);

        final String nameKey = StringUtils.isBlank(name)
            ? (controllerClass.getName() + StringConstants.DOT + method.getName()).replace("$", StringConstants.DOT)
            : name;
        this.routeListPerNameMap.computeIfAbsent(nameKey, (key) -> new ArrayList<>()).add(route);
    }

    @Override
    public ResolvedRoute resolveRoute(final HttpMethod httpMethod, final String uri) {

        if (httpMethod == null || StringUtils.isEmpty(uri)) {
            return null;
        }

        final List<Route> routeList = this.routeListPerHttpMethodMap.get(httpMethod);
        if (routeList != null) {
            Matcher matcher;
            for (final Route route : routeList) {
                matcher = route.routePattern().matcher(uri);
                if (matcher.matches()) {
                    final Map<String, String> extractedParameterMap;
                    if (route.method().getParameterCount() == 0) {
                        extractedParameterMap = Collections.emptyMap();
                    } else {
                        extractedParameterMap = new HashMap<>();
                        for (final String namedGroup : getNamedGroup(route.routePattern().pattern())) {
                            extractedParameterMap.put(namedGroup, matcher.group(namedGroup));
                        }
                    }

                    return new ResolvedRoute(route.filterClassTypes(), route.controllerClassType(), route.method(), extractedParameterMap);
                }
            }
        }

        return null;
    }

    @Override
    public String reverseRoute(final String name) {

        return reverseRoute(name, Collections.emptyList());
    }

    @Override
    public String reverseRoute(final String name, final List<Object> parameterList) {

        if (StringUtils.isBlank(name)) {
            return null;
        }

        // Retrieve route from given name
        final List<Route> routeList = this.routeListPerNameMap.get(name);
        if (routeList == null) {
            return null;
        }

        for (final Route route : routeList) {
            // Build URL for the route from given parameterList
            final Matcher matcher = PATTERN_EXTRACT_REGEXP_GROUP.matcher(route.routePattern().toString());
            final Iterator<Object> iterator = parameterList.iterator();
            String url = StringUtils.EMPTY;
            while (matcher.find()) {
                url = matcher.replaceAll((matchResult) ->
                    iterator.hasNext() ? Objects.toString(iterator.next()) : StringUtils.EMPTY);
            }

            if (url.isEmpty()) {
                url += route.routePattern().toString();
            }

            // Test newly build URL against the route to validate arguments.
            if (route.routePattern().matcher(url).matches()) {
                return url;
            }
        }

        // Impossible to build a route with the arguments provided
        return null;
    }

    @Override
    public List<Route> getRoutesAsList() {

        final List<Route> routeList = Stream.of(HttpMethod.values())
            .map(routeListPerHttpMethodMap::get)
            .filter(Objects::nonNull)
            .flatMap(List::stream)
            .toList();

        return ImmutableList.copyOf(routeList);
    }

    @Override
    public Map<HttpMethod, List<Route>> getRoutesAsMap() {

        return ImmutableMap.copyOf(routeListPerHttpMethodMap);
    }

    @Override
    public void onPostInitialization() {

        final Comparator<Route> regexIdxComparator = Comparator.comparingInt(obj -> {
            final int idx = obj.routePattern().toString().indexOf("(");
            return idx < 0 ? Integer.MAX_VALUE : idx;
        });
        final Comparator<Route> regexCaptureIdxComparator = Comparator.comparingInt(obj -> {
            final int idx = obj.routePattern().toString().indexOf("(?");
            return idx < 0 ? Integer.MIN_VALUE : idx;
        });
        final Comparator<Route> lengthComparator = Comparator.comparing(obj -> obj.routePattern().toString().length());
        final Comparator<Route> alphaComparator = Comparator.comparing(Route::toString);
        final Comparator<Route> routeComparator = regexIdxComparator.reversed()
            .thenComparing(regexCaptureIdxComparator)
            .thenComparing(lengthComparator)
            .thenComparing(alphaComparator);

        for (final Map.Entry<HttpMethod, List<Route>> entry : this.routeListPerHttpMethodMap.entrySet()) {
            entry.getValue().sort(routeComparator);
        }
    }

    /**
     * Checks given arguments are valid.
     * This method will throw an exception if any argument is invalid.
     *
     * @param httpMethod          The HTTP method (ie: GET)
     * @param routeURL            The route url
     * @param controllerClassType The controller class type
     * @param method              The method to call
     */
    private void checkAddRouteArguments(final HttpMethod httpMethod,
                                        final RouteURL routeURL,
                                        final Class<?> controllerClassType,
                                        final Method method) {

        if (httpMethod == null) {
            throw new RoutingException.BadRoutingArgument("httpMethod", null);
        } else if (routeURL == null || routeURL.url().isEmpty()) {
            throw new RoutingException.BadRoutingArgument("routeUrl", routeURL);
        } else if (controllerClassType == null) {
            throw new RoutingException.BadRoutingArgument("controllerClassType", null);
        } else if (method == null) {
            throw new RoutingException.BadRoutingArgument("method", null);
        }
    }
}
