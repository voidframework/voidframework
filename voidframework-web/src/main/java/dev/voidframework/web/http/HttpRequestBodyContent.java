package dev.voidframework.web.http;

import com.fasterxml.jackson.databind.JsonNode;
import dev.voidframework.core.helper.Json;
import dev.voidframework.core.helper.Reflection;
import dev.voidframework.core.helper.Xml;
import dev.voidframework.core.helper.Yaml;
import dev.voidframework.web.exception.HttpException;
import org.w3c.dom.Document;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An Http request body content
 *
 * @param asRaw The body content as raw
 */
public record HttpRequestBodyContent(String contentType,
                                     byte[] asRaw,
                                     Map<String, List<FormItem>> asFormData) {

    /**
     * Returns the form data content body as a specific object.
     *
     * @param outputClass         The requested Java object type
     * @param <OUTPUT_CLASS_TYPE> The requested Java class type
     * @return A Java object
     */
    public <OUTPUT_CLASS_TYPE> OUTPUT_CLASS_TYPE asFormData(final Class<OUTPUT_CLASS_TYPE> outputClass) {

        final Map<String, String> flatValueMap = new HashMap<>();
        final Map<String, InputStream> flatFileMap = new HashMap<>();

        for (final Map.Entry<String, List<FormItem>> entry : asFormData.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                if (!entry.getValue().get(0).isFile()) {
                    flatValueMap.put(entry.getKey(), entry.getValue().get(0).value());
                } else {
                    flatFileMap.put(entry.getKey(), entry.getValue().get(0).inputStream());
                }
            }
        }

        final OUTPUT_CLASS_TYPE output = Json.fromMap(flatValueMap, outputClass);

        for (final Map.Entry<String, InputStream> entry : flatFileMap.entrySet()) {
            Reflection.setFieldValue(output, entry.getKey(), entry.getValue());
        }

        return output;
    }

    /**
     * Returns the JSON content body.
     *
     * @return A JSON node
     */
    public JsonNode asJson() {

        return Json.toJson(asRaw);
    }

    /**
     * Returns the body content as a specific object.
     *
     * @param outputClass         The requested Java object type
     * @param <OUTPUT_CLASS_TYPE> The requested Java class type
     * @return A Java object
     */
    public <OUTPUT_CLASS_TYPE> OUTPUT_CLASS_TYPE as(final Class<OUTPUT_CLASS_TYPE> outputClass) {

        return switch (contentType()) {
            case HttpContentType.APPLICATION_JSON -> Json.fromJson(this.asRaw, outputClass);
            case HttpContentType.APPLICATION_X_FORM_URLENCODED, HttpContentType.MULTIPART_FORM_DATA -> asFormData(outputClass);
            case HttpContentType.APPLICATION_XML -> Xml.fromXml(this.asRaw, outputClass);
            case HttpContentType.TEXT_YAML -> Yaml.fromYaml(this.asRaw, outputClass);
            default -> throw new HttpException.BadRequest("Unhandled body content");
        };
    }

    /**
     * Returns the XML content body.
     *
     * @return An XML document
     */
    public Document asXml() {

        return Xml.toXml(asRaw);
    }

    /**
     * Returns the YAML content body.
     *
     * @return A YAML node
     */
    public JsonNode asYaml() {

        return Yaml.toYaml(asRaw);
    }
}
