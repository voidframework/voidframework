package com.voidframework.web.http.impl;

import com.voidframework.web.exception.HttpException;
import com.voidframework.web.http.Context;
import com.voidframework.web.http.ErrorHandler;
import com.voidframework.web.http.Result;

/**
 * Default implementation of {@link ErrorHandler}.
 */
public class DefaultErrorHandler implements ErrorHandler {

    @Override
    public Result onNotFound(final Context context, final HttpException.NotFound notFoundException) {
        return Result.notFound("404 Not Found");
    }

    @Override
    public Result onServerError(final Context context, final Throwable throwable) {
        return Result.internalServerError("500 Internal Server Error");
    }
}
