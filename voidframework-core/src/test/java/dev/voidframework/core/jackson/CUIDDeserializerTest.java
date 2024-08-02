package dev.voidframework.core.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import dev.voidframework.core.lang.CUID;
import dev.voidframework.core.utils.JsonUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class CUIDDeserializerTest {

    private static final JavaType LIST_CUID_JAVA_TYPE = JsonUtils.objectMapper().constructType(new TypeReference<List<CUID>>() {
    });

    @Test
    void customTypeCUIDDeserializer() {

        // Arrange
        final String jsonDocumentAsString = "[\"cl9hjgoq500000047op34bbb4\",\"cl9hjgos200010047m6f4i9s0\"]";

        // Act
        final List<CUID> cuidList = JsonUtils.fromJson(jsonDocumentAsString, LIST_CUID_JAVA_TYPE);

        // Assert
        Assertions.assertEquals(CUID.fromString("cl9hjgoq500000047op34bbb4"), cuidList.get(0));
        Assertions.assertEquals(CUID.fromString("cl9hjgos200010047m6f4i9s0"), cuidList.get(1));
    }
}
