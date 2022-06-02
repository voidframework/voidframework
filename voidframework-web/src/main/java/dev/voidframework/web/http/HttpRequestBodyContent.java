package dev.voidframework.web.http;

import com.fasterxml.jackson.databind.JsonNode;
import dev.voidframework.core.helper.Json;
import dev.voidframework.core.helper.Xml;
import org.w3c.dom.Document;

import java.util.List;
import java.util.Map;

/**
 * An Http request body content
 *
 * @param asRaw The body content as raw
 */
public record HttpRequestBodyContent(byte[] asRaw,
                                     Map<String, List<dev.voidframework.web.http.FormItem>> asFormData) {

    /**
     * Returns the JSON content body.
     *
     * @return A JSON node
     */
    public JsonNode asJson() {
        return Json.toJson(asRaw);
    }

    /**
     * Returns the JSON content body as a specific object.
     *
     * @param outputClass         The requested Java object type
     * @param <OUTPUT_CLASS_TYPE> The requested Java class type
     * @return A Java object
     */
    public <OUTPUT_CLASS_TYPE> OUTPUT_CLASS_TYPE asJson(final Class<OUTPUT_CLASS_TYPE> outputClass) {
        return Json.fromJson(asJson(), outputClass);
    }

    /**
     * Returns the XML content body.
     *
     * @return An XML document
     */
    public Document asXml() {
        return Xml.toXml(asRaw);
    }
}
