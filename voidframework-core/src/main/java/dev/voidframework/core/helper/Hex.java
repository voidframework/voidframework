package dev.voidframework.core.helper;

/**
 * String utility methods.
 */
public final class Hex {

    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    /**
     * Convert each byte composing given string into hexadecimal representation.
     *
     * @param str String to convert
     * @return Hexadecimal representation
     */
    public static String toHex(final String str) {

        return toHex(str.getBytes());
    }

    /**
     * Convert each byte composing given array into hexadecimal representation.
     *
     * @param byteArray A byte array
     * @return Hexadecimal representation
     */
    public static String toHex(final byte[] byteArray) {

        final StringBuilder stringBuilder = new StringBuilder();

        for (final byte b : byteArray) {
            final int octet = b & 0xFF;

            stringBuilder.append(HEX_CHARS[(octet & 0xF0) >> 4]);
            stringBuilder.append(HEX_CHARS[octet & 0x0F]);
        }

        return stringBuilder.toString();
    }
}
