package dev.voidframework.core.helper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.nio.charset.StandardCharsets;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class HexTest {

    @Test
    public void toHexString() {

        Assertions.assertEquals("48656c6c6f20576f726c6421", Hex.toHex("Hello World!"));
    }

    @Test
    public void toHexByteArray() {

        Assertions.assertEquals("48656c6c6f20576f726c6421", Hex.toHex("Hello World!".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void toHexNull() {

        Assertions.assertNull(Hex.toHex((String) null));
        Assertions.assertNull(Hex.toHex((byte[]) null));
    }
}
