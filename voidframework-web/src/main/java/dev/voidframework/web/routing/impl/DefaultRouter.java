package dev.voidframework.web.routing.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import dev.voidframework.core.helper.ProxyDetector;
import dev.voidframework.web.exception.RoutingException;
import dev.voidframework.web.filter.Filter;
import dev.voidframework.web.filter.WithFilter;
import dev.voidframework.web.routing.HttpMethod;
import dev.voidframework.web.routing.ResolvedRoute;
import dev.voidframework.web.routing.Route;
import dev.voidframework.web.routing.Router;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default implementation of {@link Router}.
 */
public class DefaultRouter implements Router {

    private static final Logger LOGGER = LoggerFactory.getLogger(Router.class);
    private static final Pattern PATTERN_EXTRACT_REGEXP_GROUP = Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z\\d]*)>.*\\)");
    private static final Pattern PATTERN_EXTRACT_REGEXP_GROUP_NAME = Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z\\d]*)>");

    private final Map<HttpMethod, List<Route>> routeListPerHttpMethodMap;
    private final Map<String, List<Route>> routeListPerNameMap;

    /**
     * Build a new instance.
     */
    public DefaultRouter() {

        this.routeListPerHttpMethodMap = new HashMap<>();
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
                         final String routeUrl,
                         final Class<?> controllerClassType,
                         final Method method) {
        this.addRoute(httpMethod, routeUrl, controllerClassType, method, StringUtils.EMPTY);
    }

    @Override
    public void addRoute(final HttpMethod httpMethod,
                         final String routeUrl,
                         final Class<?> controllerClassType,
                         final Method method,
                         final String name) {

        this.checkAddRouteArguments(httpMethod, routeUrl, controllerClassType, method);

        final Class<?> controllerClass = ProxyDetector.isProxy(controllerClassType)
            ? controllerClassType.getSuperclass()
            : controllerClassType;
        LOGGER.debug("Add route {} {} {}::{}", httpMethod, routeUrl, controllerClass.getName(), method.getName());

        final List<Class<? extends Filter>> filterClassList = new ArrayList<>();
        WithFilter withFilter = controllerClass.getAnnotation(WithFilter.class);
        if (withFilter != null) {
            filterClassList.addAll(Arrays.asList(withFilter.value()));
        }

        withFilter = method.getAnnotation(WithFilter.class);
        if (withFilter != null) {
            filterClassList.addAll(Arrays.asList(withFilter.value()));
        }

        final Route route = new Route(httpMethod, Pattern.compile(routeUrl), filterClassList, controllerClass, method);
        this.routeListPerHttpMethodMap.computeIfAbsent(httpMethod, (key) -> new ArrayList<>()).add(route);

        final String nameKey = StringUtils.isBlank(name)
            ? (controllerClass.getName() + "." + method.getName()).replace("$", ".")
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

        final List<Route> routeList = routeListPerHttpMethodMap.values()
            .stream()
            .flatMap(List::stream)
            .toList();

        return ImmutableList.copyOf(routeList);
    }

    @Override
    public Map<HttpMethod, List<Route>> getRoutesAsMap() {

        return ImmutableMap.copyOf(routeListPerHttpMethodMap);
    }

    /**
     * Checks given arguments are valid.
     * This method will throw an exception if any argument is invalid.
     *
     * @param httpMethod          The HTTP method (ie: GET)
     * @param routeUrl            The route url
     * @param controllerClassType The controller class type
     * @param method              The method to call
     */
    private void checkAddRouteArguments(final HttpMethod httpMethod,
                                        final String routeUrl,
                                        final Class<?> controllerClassType,
                                        final Method method) {

        if (httpMethod == null) {
            throw new RoutingException.BadRoutingArgument("httpMethod", null);
        } else if (routeUrl == null || (routeUrl.length() != routeUrl.trim().length())) {
            throw new RoutingException.BadRoutingArgument("routeUrl", routeUrl);
        } else if (controllerClassType == null) {
            throw new RoutingException.BadRoutingArgument("controllerClassType", null);
        } else if (method == null) {
            throw new RoutingException.BadRoutingArgument("method", null);
        }
    }
}
