package com.voidframework.core.routing.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.voidframework.core.exception.BadRouteDefinitionException;
import com.voidframework.core.routing.HttpMethod;
import com.voidframework.core.routing.Route;
import com.voidframework.core.routing.RouteBuilder;
import com.voidframework.core.routing.Router;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class DefaultRouter implements Router {

    private static final Logger LOGGER = LoggerFactory.getLogger(Router.class);

    private final Map<HttpMethod, List<Route>> routeListPerHttpMethodMap;

    /**
     * Build a new instance.
     */
    public DefaultRouter() {
        this.routeListPerHttpMethodMap = new HashMap<>();
    }

    @Override
    public void addRoute(final Function<RouteBuilder, RouteBuilder> routeBuilderFunction) {
        DefaultRouteBuilder routeBuilder = new DefaultRouteBuilder();
        routeBuilder = (DefaultRouteBuilder) routeBuilderFunction.apply(routeBuilder);

        final Route route = validateAndCreateRoute(routeBuilder);
        LOGGER.info("Add route {} {} {}::{}", route.httpMethod, route.routePattern, route.controllerClass.getName(), route.method.getName());
        this.routeListPerHttpMethodMap.computeIfAbsent(route.httpMethod, (key) -> new ArrayList<>()).add(route);
    }

    @Override
    public Optional<Route> resolveRoute(final HttpMethod httpMethod, final String uri) {
        if (httpMethod == null || StringUtils.isEmpty(uri)) {
            return Optional.empty();
        }

        final List<Route> routeList = this.routeListPerHttpMethodMap.get(httpMethod);
        if (routeList != null) {
            return routeList
                .stream()
                .filter(route -> route.routePattern.matcher(uri).matches())
                .findFirst();
        }

        return Optional.empty();
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

    private Route validateAndCreateRoute(final DefaultRouteBuilder routeBuilder) {
        final HttpMethod httpMethod = routeBuilder.getHttpMethod();
        if (httpMethod == null) {
            throw new BadRouteDefinitionException.Missing("method");
        }

        final Class<?> controllerClass = routeBuilder.getControllerClass();
        if (controllerClass == null) {
            throw new BadRouteDefinitionException.Missing("controllerClass");
        }

        final String methodName = routeBuilder.getMethodName();
        if (StringUtils.isEmpty(methodName)) {
            throw new BadRouteDefinitionException.Missing("methodName");
        }

        final Pattern routePattern;
        try {
            routePattern = Pattern.compile(routeBuilder.getRoute());
        } catch (final PatternSyntaxException ex) {
            throw new BadRouteDefinitionException.BadValue("route", "Can't compile regular expression", ex);
        } catch (final NullPointerException ignore) {
            throw new BadRouteDefinitionException.Missing("route");
        }

        for (final Method method : controllerClass.getMethods()) {
            if (method.getName().equals(methodName)) {
                if (method.getReturnType() == void.class) {
                    throw new BadRouteDefinitionException.ControllerMethodDoesNotReturnsValue(controllerClass, methodName);
                }

                return new Route(httpMethod, routePattern, controllerClass, method);
            }
        }

        throw new BadRouteDefinitionException.ControllerMethodDoesNotExists(controllerClass, methodName);
    }
}
