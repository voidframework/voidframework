package com.voidframework.web.http.controller;

import com.google.inject.Singleton;
import com.voidframework.web.exception.HttpException;
import com.voidframework.web.http.Result;
import com.voidframework.web.http.param.RequestPath;

@Singleton
public final class AssetController {

    public Result getAsset(@RequestPath("fileName") final String fileName) {
       throw new HttpException.NotFound();
    }
}
