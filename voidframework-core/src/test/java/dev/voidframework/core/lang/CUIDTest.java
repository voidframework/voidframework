package dev.voidframework.core.lang;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.HashSet;
import java.util.Set;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class CUIDTest {

    @Test
    void fromString() {

        // Arrange
        final String cuidAsString = "cl9gts1kw00393647w1z4v2tc";

        // Act
        final CUID cuid = CUID.fromString(cuidAsString);

        // Assert
        Assertions.assertNotNull(cuid);
        Assertions.assertEquals(25, cuid.toString().length());
        Assertions.assertEquals("cl9gts1kw00393647w1z4v2tc", cuid.toString());
    }

    @Test
    void randomCUID() {

        // Act
        final CUID cuid = CUID.randomCUID();

        // Assert
        Assertions.assertNotNull(cuid);
        Assertions.assertEquals(25, cuid.toString().length());
    }

    @Test
    void unicityOver500000() {

        // Act
        final Set<CUID> cuidSet = new HashSet<>();
        for (int i = 0; i < 500000; i += 1) {
            cuidSet.add(CUID.randomCUID());
        }

        // Assert
        Assertions.assertEquals(500000, cuidSet.size());
    }
}
