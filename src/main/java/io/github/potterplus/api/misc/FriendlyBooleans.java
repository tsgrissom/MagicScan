package io.github.potterplus.api.misc;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * A simple class for handling user-friendly forms of boolean value input.
 */
public class FriendlyBooleans {

    private static final List<String> trueValues = ImmutableList.of("true", "yes", "enable");
    private static final List<String> falseValues = ImmutableList.of("false", "no", "disable");

    /**
     * Checks if the provided String is any of the friendly boolean values.
     * @param s The String to check.
     * @return Whether or not the String is a friendly boolean.
     */
    public static boolean isFriendlyBoolean(String s) {
        s = s.toLowerCase();

        return trueValues.contains(s) || falseValues.contains(s);
    }

    /**
     * Parses a boolean value from the friendly boolean String.
     * Do this after checking if the String is a friendly boolean.
     * @param s The String to parse.
     * @return The boolean value.
     */
    public static boolean getFriendlyBoolean(String s) {
        s = s.toLowerCase();

        return trueValues.contains(s);
    }
}
