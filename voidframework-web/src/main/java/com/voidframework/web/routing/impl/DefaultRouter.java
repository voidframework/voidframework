package com.voidframework.web.routing.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.voidframework.core.helper.ProxyDetector;
import com.voidframework.web.routing.HttpMethod;
import com.voidframework.web.routing.ResolvedRoute;
import com.voidframework.web.routing.Route;
import com.voidframework.web.routing.Router;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default implementation of {@link Router}.
 */
public class DefaultRouter implements Router {

    private static final Logger LOGGER = LoggerFactory.getLogger(Router.class);
    private static final Pattern PATTERN_EXTRACT_REGEXP_GROUP_NAME = Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z\\d]*)>");

    private final Map<HttpMethod, List<Route>> routeListPerHttpMethodMap;

    /**
     * Build a new instance.
     */
    public DefaultRouter() {
        this.routeListPerHttpMethodMap = new HashMap<>();
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

        final Class<?> controllerClass = ProxyDetector.isProxy(controllerClassType)
            ? controllerClassType.getSuperclass()
            : controllerClassType;
        LOGGER.debug("Add route {} {} {}::{}", httpMethod, routeUrl, controllerClass.getName(), method.getName());

        final Route route = new Route(httpMethod, Pattern.compile(routeUrl), controllerClass, method);
        this.routeListPerHttpMethodMap.computeIfAbsent(httpMethod, (key) -> new ArrayList<>()).add(route);
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

                    return new ResolvedRoute(route.controllerClassType(), route.method(), extractedParameterMap);
                }
            }
        }

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
}
