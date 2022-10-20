package dev.voidframework.core.jackson;

import dev.voidframework.core.lang.CUID;
import dev.voidframework.core.utils.JsonUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class CUIDSerializerTest {

    @Test
    void cuidSerializer() {

        // Arrange
        final List<CUID> cuidList = List.of(
            CUID.fromString("cl9hjgoq500000047op34bbb4"),
            CUID.fromString("cl9hjgos200010047m6f4i9s0"));

        // Act
        final String jsonDocumentAsString = JsonUtils.toString(cuidList);

        // Assert
        Assertions.assertEquals("[\"cl9hjgoq500000047op34bbb4\",\"cl9hjgos200010047m6f4i9s0\"]", jsonDocumentAsString);
    }
}
