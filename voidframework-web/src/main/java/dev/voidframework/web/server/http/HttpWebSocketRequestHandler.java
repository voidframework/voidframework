package dev.voidframework.web.server.http;

import com.google.inject.Injector;
import dev.voidframework.core.conversion.Conversion;
import dev.voidframework.web.http.HttpMethod;
import dev.voidframework.web.http.WebSocketContext;
import dev.voidframework.web.http.annotation.RequestPath;
import dev.voidframework.web.http.annotation.RequestVariable;
import dev.voidframework.web.http.routing.ResolvedRoute;
import dev.voidframework.web.http.routing.Router;
import io.undertow.websockets.core.WebSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Parameter;

/**
 * WebSocket incoming connection handler.
 *
 * @since 1.3.0
 */
public final class HttpWebSocketRequestHandler extends AbstractHttpRequestHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpWebSocketRequestHandler.class);

    private final Injector injector;
    private final Router router;

    /**
     * Build a new instance.
     *
     * @param injector The injector instance
     * @since 1.3.0
     */
    public HttpWebSocketRequestHandler(final Injector injector) {

        super(injector.getInstance(Conversion.class));

        this.injector = injector;
        this.router = this.injector.getInstance(Router.class);
    }

    /**
     * This method is called each time the framework need to handle an incoming WebSocket connection.
     *
     * @param context The WebSocket context
     * @throws IOException if something goes wrong with the newly open connection
     * @since 1.3.0
     */
    public void onIncomingConnection(final WebSocketContext context) throws IOException {

        // Tries to resolve route
        final ResolvedRoute resolvedRoute = router.resolveRoute(HttpMethod.WEBSOCKET, context.getRequest().getRequestURI());
        if (resolvedRoute == null) {
            context.getChannel().sendClose();
            return;
        }

        // Call the right controller method
        final Object controllerInstance = injector.getInstance(resolvedRoute.controllerClassType());
        final Object[] methodArgumentValueArray = buildMethodArguments(context, resolvedRoute);

        try {
            resolvedRoute.method().invoke(controllerInstance, methodArgumentValueArray);
        } catch (final Exception exception) {
            LOGGER.error("Something wrong occurred when handling incoming WebSocket connection", exception);
            context.getChannel().sendClose();
        }
    }

    /**
     * Builds method arguments.
     *
     * @param context       The current context
     * @param resolvedRoute The resolved route
     * @return An array containing method arguments
     * @since 1.3.0
     */
    private Object[] buildMethodArguments(final WebSocketContext context, final ResolvedRoute resolvedRoute) {

        int idx = 0;
        final Object[] methodArgumentValueArray = new Object[resolvedRoute.method().getParameterCount()];

        for (final Parameter parameter : resolvedRoute.method().getParameters()) {
            if (parameter.getType().isAssignableFrom(WebSocketContext.class)) {
                methodArgumentValueArray[idx] = context;
            } else if (parameter.getType().isAssignableFrom(WebSocketChannel.class)) {
                methodArgumentValueArray[idx] = context.getChannel();
            } else {
                final RequestPath requestPath = parameter.getAnnotation(RequestPath.class);
                final RequestVariable requestVariable = parameter.getAnnotation(RequestVariable.class);

                if (requestPath != null) {
                    methodArgumentValueArray[idx] = convertValueToParameterType(
                        resolvedRoute.extractedParameterValues().getOrDefault(requestPath.value(), null),
                        parameter.getType());
                } else if (requestVariable != null) {
                    methodArgumentValueArray[idx] = convertValueToParameterType(
                        context.getRequest().getQueryStringParameter(
                            requestVariable.value(),
                            EMPTY_FALLBACK_VALUE.equals(requestVariable.fallback()) ? null : requestVariable.fallback()),
                        parameter.getType());
                } else {
                    methodArgumentValueArray[idx] = this.injector.getInstance(parameter.getType());
                }
            }

            idx += 1;
        }

        return methodArgumentValueArray;
    }
}
