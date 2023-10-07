package dev.voidframework.web.server.http;

import com.google.inject.Injector;
import com.typesafe.config.Config;
import dev.voidframework.core.constant.StringConstants;
import dev.voidframework.core.conversion.Conversion;
import dev.voidframework.template.TemplateRenderer;
import dev.voidframework.web.exception.HttpException;
import dev.voidframework.web.http.Context;
import dev.voidframework.web.http.Result;
import dev.voidframework.web.http.annotation.NoCSRF;
import dev.voidframework.web.http.annotation.RequestBody;
import dev.voidframework.web.http.annotation.RequestPath;
import dev.voidframework.web.http.annotation.RequestVariable;
import dev.voidframework.web.http.errorhandler.ErrorHandler;
import dev.voidframework.web.http.filter.DefaultFilterChain;
import dev.voidframework.web.http.filter.Filter;
import dev.voidframework.web.http.filter.FilterChain;
import dev.voidframework.web.http.filter.csrf.CSRFFilter;
import dev.voidframework.web.http.routing.ResolvedRoute;
import dev.voidframework.web.http.routing.Router;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Http request handler.
 *
 * @since 1.0.0
 */
public final class HttpRequestHandler extends AbstractHttpRequestHandler {

    private final Injector injector;
    private final List<Class<? extends Filter>> globalFilterClassTypes;

    private final ErrorHandler errorHandler;
    private final Router router;
    private final Config configuration;
    private TemplateRenderer templateRenderer;

    /**
     * Build a new instance.
     *
     * @param injector               The injector instance
     * @param errorHandler           The error handler to use
     * @param globalFilterClassTypes The global filter class types
     * @since 1.0.0
     */
    public HttpRequestHandler(final Injector injector,
                              final ErrorHandler errorHandler,
                              final List<Class<? extends Filter>> globalFilterClassTypes) {

        super(injector.getInstance(Conversion.class));

        this.injector = injector;
        this.errorHandler = errorHandler;
        this.globalFilterClassTypes = globalFilterClassTypes;
        this.router = this.injector.getInstance(Router.class);
        this.configuration = this.injector.getInstance(Config.class);
        try {
            this.templateRenderer = this.injector.getInstance(TemplateRenderer.class);
        } catch (final Exception ignore) {
            this.templateRenderer = null;
        }
    }

    /**
     * This method is called each time a bad request occur.
     *
     * @param context The current context
     * @param cause   The cause (OPTIONAL)
     * @return A result
     * @since 1.0.0
     */
    public Result onBadRequest(final Context context, final HttpException.BadRequest cause) {

        return errorHandler.onBadRequest(context, cause);
    }

    /**
     * This method is called each time the framework need to route a request.
     *
     * @param context The current context
     * @return A result
     * @since 1.0.0
     */
    public Result onRouteRequest(final Context context) {

        // The processing carried out here is based exclusively on the chaining of Filters, some
        // of which are conditioned according to whether the route is found. This way, global
        // filters will always be executed, even on error pages
        final List<Filter> filterList = new ArrayList<>();

        // Instantiates global filters
        for (final Class<? extends Filter> filterClassType : this.globalFilterClassTypes) {
            filterList.add(this.injector.getInstance(filterClassType));
        }

        // Tries to resolve route
        final ResolvedRoute resolvedRoute = router.resolveRoute(context.getRequest().getHttpMethod(), context.getRequest().getRequestURI());
        if (resolvedRoute == null) {
            // CSRF checking not needed on 404 error page
            context.getAttributes().put(CSRFFilter.BYPASS_CSRF_VERIFICATION, Boolean.TRUE);

            // No route found, only the Filter showing the "404" error page is required
            final Filter callNotFoundFilter = (ctx, filterChain) -> {
                final Result result = errorHandler.onNotFound(ctx, null);
                result.getResultProcessor().process(ctx, configuration, templateRenderer);

                return result;
            };
            filterList.add(callNotFoundFilter);
        } else {
            // CSRF Modifier
            if (resolvedRoute.controllerClassType().isAnnotationPresent(NoCSRF.class)
                || resolvedRoute.method().isAnnotationPresent(NoCSRF.class)) {
                context.getAttributes().put(CSRFFilter.BYPASS_CSRF_VERIFICATION, Boolean.TRUE);
            }

            // Instantiates controller and method filters
            for (final Class<? extends Filter> filterClassType : resolvedRoute.filterClassTypes()) {
                filterList.add(this.injector.getInstance(filterClassType));
            }

            // Instantiates the Filter in charge of calling the controller method with the
            // right arguments and handling possible errors
            final Filter callControllerFile = (ctx, filterChain) -> {
                final Object controllerInstance = injector.getInstance(resolvedRoute.controllerClassType());

                try {
                    final Result result;
                    if (resolvedRoute.method().getParameterCount() == 0) {
                        // No parameters, just invoke the controller method
                        result = (Result) resolvedRoute.method().invoke(controllerInstance);
                    } else {
                        // Method have some parameter(s)
                        final Object[] methodArgumentValueArray = buildMethodArguments(ctx, resolvedRoute);
                        result = (Result) resolvedRoute.method().invoke(controllerInstance, methodArgumentValueArray);
                    }

                    result.getResultProcessor().process(ctx, configuration, templateRenderer);
                    return result;

                } catch (final Exception exception) {
                    final Throwable cause = exception.getCause() == null ? exception : exception.getCause();

                    final Result result;
                    if (cause instanceof HttpException.NotFound causeAsNotFound) {
                        result = errorHandler.onNotFound(ctx, causeAsNotFound);
                    } else if (cause instanceof HttpException.BadRequest causeAsBadRequest) {
                        result = errorHandler.onBadRequest(ctx, causeAsBadRequest);
                    } else {
                        result = errorHandler.onServerError(ctx, exception);
                    }

                    result.getResultProcessor().process(ctx, configuration, templateRenderer);

                    return result;
                }
            };

            filterList.add(callControllerFile);
        }

        try {
            // Process the entire Filters chain
            final FilterChain filterChain = new DefaultFilterChain(filterList);
            return filterChain.applyNext(context);
        } catch (final Exception exception) {
            final Result result = errorHandler.onServerError(context, exception);
            result.getResultProcessor().process(context, configuration, templateRenderer);
            return result;
        }
    }

    /**
     * Builds method arguments.
     *
     * @param context       The current context
     * @param resolvedRoute The resolved route
     * @return An array containing method arguments
     * @since 1.0.0
     */
    private Object[] buildMethodArguments(final Context context, final ResolvedRoute resolvedRoute) {

        int idx = 0;
        final Object[] methodArgumentValueArray = new Object[resolvedRoute.method().getParameterCount()];

        for (final Parameter parameter : resolvedRoute.method().getParameters()) {
            if (parameter.getType().isAssignableFrom(Context.class)) {
                methodArgumentValueArray[idx] = context;
                idx += 1;
                continue;
            }

            final RequestBody requestBody = parameter.getAnnotation(RequestBody.class);
            final RequestPath requestPath = parameter.getAnnotation(RequestPath.class);
            final RequestVariable requestVariable = parameter.getAnnotation(RequestVariable.class);

            if (requestBody != null) {
                if (context.getRequest().getBodyContent().contentType() != null) {
                    methodArgumentValueArray[idx] = context.getRequest().getBodyContent().as(parameter.getType());
                } else {
                    methodArgumentValueArray[idx] = null;
                }
            } else if (requestPath != null) {
                methodArgumentValueArray[idx] = convertValueToParameterType(
                    resolvedRoute.extractedParameterValues().getOrDefault(requestPath.value(), null),
                    parameter.getType());
            } else if (requestVariable != null) {
                if (parameter.getType().isArray()) {
                    List<String> paramValueList = context.getRequest().getQueryStringParameterAsList(requestVariable.value());
                    if (paramValueList.isEmpty() && !EMPTY_FALLBACK_VALUE.equals(requestVariable.fallback())) {
                        paramValueList = Arrays.asList(
                            StringUtils.splitByWholeSeparator(requestVariable.fallback(), StringConstants.COMMA));
                    }

                    methodArgumentValueArray[idx] = convertValuesToParameterTypeArray(paramValueList, parameter.getType().componentType());
                } else {
                    methodArgumentValueArray[idx] = convertValueToParameterType(
                        context.getRequest().getQueryStringParameter(
                            requestVariable.value(),
                            EMPTY_FALLBACK_VALUE.equals(requestVariable.fallback()) ? null : requestVariable.fallback()),
                        parameter.getType());
                }
            } else {
                methodArgumentValueArray[idx] = this.injector.getInstance(parameter.getType());
            }

            idx += 1;
        }

        return methodArgumentValueArray;
    }
}
