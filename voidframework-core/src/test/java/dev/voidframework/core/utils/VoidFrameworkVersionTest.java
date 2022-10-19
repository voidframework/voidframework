package dev.voidframework.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class VoidFrameworkVersionTest {

    @Test
    void version() {

        // Act
        final String version = VoidFrameworkVersion.getVersion();

        // Assert
        Assertions.assertNotNull(version);
        Assertions.assertFalse(version.isBlank());

        final String[] versionArray = version.split("\\.");
        Assertions.assertEquals(3, versionArray.length);
        Assertions.assertTrue(NumberUtils.isDigits(versionArray[0]));
        Assertions.assertTrue(NumberUtils.isDigits(versionArray[1]));
        Assertions.assertTrue(NumberUtils.isDigits(versionArray[2].replace("-SNAPSHOT", StringUtils.EMPTY)));
    }
}
