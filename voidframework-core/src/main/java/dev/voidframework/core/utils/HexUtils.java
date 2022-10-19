package dev.voidframework.core.utils;

/**
 * Utility methods to handle hexadecimal values.
 */
public final class HexUtils {

    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    /**
     * Default constructor.
     */
    private HexUtils() {

        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Convert each byte composing given string into hexadecimal representation.
     *
     * @param str String to convert
     * @return Hexadecimal representation
     */
    public static String toHex(final String str) {

        if (str == null) {
            return null;
        }

        return toHex(str.getBytes());
    }

    /**
     * Convert each byte composing given array into hexadecimal representation.
     *
     * @param byteArray A byte array
     * @return Hexadecimal representation
     */
    public static String toHex(final byte[] byteArray) {

        if (byteArray == null) {
            return null;
        }

        final StringBuilder stringBuilder = new StringBuilder();

        for (final byte b : byteArray) {
            final int octet = b & 0xFF;

            stringBuilder.append(HEX_CHARS[(octet & 0xF0) >> 4]);
            stringBuilder.append(HEX_CHARS[octet & 0x0F]);
        }

        return stringBuilder.toString();
    }
}
