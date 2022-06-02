package dev.voidframework.web.http;

import dev.voidframework.web.exception.HttpException;

/**
 * This interface allows you to define the behaviour to have when an error occurs.
 */
public interface ErrorHandler {

    /**
     * This method is called when no route has been found to satisfy the request.
     *
     * @param context The current context
     * @param notFoundException The cause (OPTIONAL)
     * @return A result
     */
    Result onNotFound(final Context context, final HttpException.NotFound notFoundException);

    /**
     * This method is called when an unexpected error occurs during the processing of the request.
     *
     * @param context   The current context
     * @param throwable The cause
     * @return A result
     */
    Result onServerError(final Context context, final Throwable throwable);
}
