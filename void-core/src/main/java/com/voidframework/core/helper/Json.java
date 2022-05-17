package com.voidframework.core.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

/**
 * Helper to handle JSON document.
 */
public final class Json {

    public static final ObjectMapper objectMapper = JsonMapper.builder()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false)
        .addModule(new JavaTimeModule())
        .addModule(new Jdk8Module())
        .addModule(new JodaModule())
        .build();

    /**
     * Converts a JsonNode to string.
     *
     * @param json the JSON node to convert.
     * @return The string representation.
     */
    public static String toString(final JsonNode json) {
        try {
            final ObjectWriter writer = objectMapper.writer();
            return writer.writeValueAsString(json);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts an object to JSON document.
     *
     * @param obj Object to convert in Json
     * @return The JSON node
     */
    public static JsonNode toJson(final Object obj) {
        try {
            return objectMapper.valueToTree(obj);
        } catch (final IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts a JSON document into to a Java object.
     *
     * @param <OUTPUT_TYPE> The type of the Java object
     * @param json          Json document to convert
     * @param clazz         Expected Java object type
     * @return The Java object
     */
    public static <OUTPUT_TYPE> OUTPUT_TYPE fromJson(final JsonNode json, final Class<OUTPUT_TYPE> clazz) {
        try {
            return objectMapper.treeToValue(json, clazz);
        } catch (final IllegalArgumentException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
