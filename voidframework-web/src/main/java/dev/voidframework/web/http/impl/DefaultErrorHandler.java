package dev.voidframework.web.http.impl;

import dev.voidframework.web.exception.HttpException;
import dev.voidframework.web.http.Context;
import dev.voidframework.web.http.ErrorHandler;
import dev.voidframework.web.http.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of {@link ErrorHandler}.
 */
public class DefaultErrorHandler implements ErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandler.class);

    @Override
    public Result onBadRequest(final Context context, final HttpException.BadRequest badRequestException) {
        return Result.badRequest("400 Bad Request");
    }

    @Override
    public Result onNotFound(final Context context, final HttpException.NotFound notFoundException) {
        return Result.notFound("404 Not Found");
    }

    @Override
    public Result onServerError(final Context context, final Throwable throwable) {
        LOGGER.error("Something goes wrong during request processing", throwable);
        return Result.internalServerError("500 Internal Server Error");
    }
}
