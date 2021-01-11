package me.nahu.portals.utils;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Utilities {
    private static final char[] UPPERCASE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final Random UTILITY_RANDOM = new Random();

    /**
     * Get a random ID of 3 character length.
     * @return Random ID.
     */
    public static String getRandomId() {
        return getRandomId(3);
    }

    /**
     * Get a random id with a specified length.
     * @param length Length of the id you want.
     * @return Random ID.
     */
    public static String getRandomId(int length) {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < length; index++) {
            builder.append(UPPERCASE_ALPHABET[UTILITY_RANDOM.nextInt(UPPERCASE_ALPHABET.length)]);
        }
        return builder.toString();
    }
}
