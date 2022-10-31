package dev.voidframework.web.http;

import com.fasterxml.jackson.databind.JsonNode;
import dev.voidframework.core.constant.StringConstants;
import dev.voidframework.core.utils.IOUtils;
import dev.voidframework.core.utils.JsonUtils;
import dev.voidframework.core.utils.ReflectionUtils;
import dev.voidframework.core.utils.XmlUtils;
import dev.voidframework.core.utils.YamlUtils;
import dev.voidframework.web.exception.HttpException;
import org.w3c.dom.Document;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * An Http request body content
 *
 * @param asRaw The body content as raw
 */
public record HttpRequestBodyContent(String contentType,
                                     InputStream asRaw,
                                     FormData asFormData) {

    /**
     * Returns the body content as a specific object.
     *
     * @param outputClass The requested Java object type
     * @param <T>         The requested Java class type
     * @return A Java object
     */
    public <T> T as(final Class<T> outputClass) {

        final T object = switch (contentType()) {
            case HttpContentTypes.APPLICATION_JSON -> JsonUtils.fromJson(this.asRaw, outputClass);
            case HttpContentTypes.APPLICATION_X_FORM_URLENCODED, HttpContentTypes.MULTIPART_FORM_DATA -> asFormData(outputClass);
            case HttpContentTypes.APPLICATION_XML -> XmlUtils.fromXml(this.asRaw, outputClass);
            case HttpContentTypes.TEXT_YAML -> YamlUtils.fromYaml(this.asRaw, outputClass);
            default -> throw new HttpException.BadRequest("Unhandled body content");
        };

        IOUtils.resetWithoutException(this.asRaw);

        return object;
    }

    /**
     * Returns the form data content body as a specific object.
     *
     * @param outputClass The requested Java object type
     * @param <T>         The requested Java class type
     * @return A Java object
     */
    public <T> T asFormData(final Class<T> outputClass) {

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

        final T output = JsonUtils.fromMap(flatValueMap, outputClass);

        for (final Map.Entry<String, InputStream> entry : flatFileMap.entrySet()) {
            ReflectionUtils.setFieldValue(output, entry.getKey(), entry.getValue());
        }

        return output;
    }

    /**
     * Returns the JSON content body.
     *
     * @return A JSON node
     */
    public JsonNode asJson() {

        final JsonNode node = JsonUtils.toJson(asRaw);
        IOUtils.resetWithoutException(asRaw);

        return node;
    }

    /**
     * Returns the XML content body.
     *
     * @return An XML document
     */
    public Document asXml() {

        final Document document = XmlUtils.toXml(asRaw);
        IOUtils.resetWithoutException(asRaw);

        return document;
    }

    /**
     * Returns the YAML content body.
     *
     * @return A YAML node
     */
    public JsonNode asYaml() {

        final JsonNode node = YamlUtils.toYaml(asRaw);
        IOUtils.resetWithoutException(asRaw);

        return node;
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final HttpRequestBodyContent that = (HttpRequestBodyContent) o;
        return Objects.equals(contentType, that.contentType) && asRaw == that.asRaw && Objects.equals(asFormData, that.asFormData);
    }

    @Override
    public int hashCode() {

        return Objects.hash(contentType, asFormData);
    }

    @Override
    public String toString() {

        return "HttpRequestBodyContent{contentType='" + contentType + StringConstants.CURLY_BRACKET_CLOSE;
    }
}
