package com.voidframework.core.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.voidframework.core.helper.Json;

import java.util.List;
import java.util.Map;

/**
 * An Http request body content
 *
 * @param asRaw The body content as raw
 */
public record HttpRequestBodyContent(byte[] asRaw,
                                     Map<String, List<FormItem>> asFormData) {

    public JsonNode asJson() {
        return Json.toJson(asRaw);
    }

    public <OUTPUT_CLASS_TYPE> OUTPUT_CLASS_TYPE asJson(final Class<OUTPUT_CLASS_TYPE> outputClass) {
        return Json.fromJson(asJson(), outputClass);
    }
}
