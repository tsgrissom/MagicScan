package io.github.potterplus.api.string;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Various utility methods for operating on Strings.
 */
public class StringUtilities {

    public static char COLOR_CHAR = '&';

    /**
     * Translates color and formatting codes.
     * @param message The message.
     * @return The colored message.
     */
    public static String translateColorCodes(String message) {
        return ChatColor.translateAlternateColorCodes(COLOR_CHAR, message);
    }

    /**
     * Translates hex color codes.
     * @param formatPrefix The prefix to the format.
     * @param formatSuffix The suffix to the format.
     * @param message The message.
     * @return The colored message.
     */
    public static String translateHexColorCodes(String formatPrefix, String formatSuffix, String message) {
        Pattern pattern = Pattern.compile(formatPrefix + "([A-Fa-f0-9]{6})" + formatSuffix);
        Matcher matcher = pattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);

        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }

        return matcher.appendTail(buffer).toString();
    }

    /**
     * Removes magic formatting codes from a String.
     * @param s The String to clean.
     * @return The magic-free String.
     */
    public static String removeMagic(String s) {
        s = s.replace("&k", "");
        s = s.replace(ChatColor.COLOR_CHAR + "k", "");

        return s;
    }

    /**
     * Translates hex colors.
     * @param message The message.
     * @return The colored message.
     */
    public static String translateHexColorCodes(String message) {
        return StringUtilities.translateHexColorCodes("&#", "#", message);
    }

    /**
     * Colors a String.
     * @param str The String to color.
     * @return The colored String.
     */
    public static String color(String str) {
        str = translateColorCodes(str);

        if (str.contains("#")) {
            str = translateHexColorCodes(str);
        }

        return str;
    }

    /**
     * Colors a List of Strings.
     * @param strings The Strings to color.
     * @return The List of colored Strings.
     */
    public static List<String> color(List<String> strings) {
        return strings.stream().map(StringUtilities::color).collect(Collectors.toList());
    }

    public static List<String> color(String... strings) {
        return StringUtilities.color(Arrays.asList(strings));
    }

    public static String strip(String str) {
        return ChatColor.stripColor(color(str));
    }

    public static List<String> strip(List<String> strings) {
        return strings.stream().map(StringUtilities::strip).collect(Collectors.toList());
    }

    public static List<String> strip(String... strings) {
        return StringUtilities.color(Arrays.asList(strings));
    }

    public static String replace(String s, Map<String, String> replace) {
        if (replace == null || replace.isEmpty()) return s;

        for (Map.Entry<String, String> entry : replace.entrySet()) {
            s = s.replace(entry.getKey(), entry.getValue());
        }

        return s;
    }

    public static List<String> replace(List<String> list, Map<String, String> replace) {
        if (replace == null || replace.isEmpty()) return list;

        List<String> newList = new ArrayList<>();

        for (String s : list) {
            newList.add(replace(s, replace));
        }

        return newList;
    }

    /**
     * Checks if the first argument equals any of the subsequent arguments while ignoring String case.
     * @param str The String to compare.
     * @param any The Strings to compare against.
     * @return Whether or not the first String equals any of the subsequent Strings, case ignored.
     */
    public static boolean equalsAny(String str, String... any) {
        for (String s : any) {
            if (s.equalsIgnoreCase(str)) return true;
        }

        return false;
    }

    /**
     * Formats enum names to be pretty.
     * @param enumName The enum name to format.
     * @return The formatted enum name.
     */
    public static String getPrettyEnumName(String enumName) {
        enumName = enumName.toLowerCase();
        enumName = enumName.replace("_", " ");
        String[] split = enumName.split(" ");
        StringBuilder newStr = new StringBuilder();

        for (String s : split) {
            newStr.append(String.valueOf(s.charAt(0)).toUpperCase())
                    .append(s.substring(1))
                    .append(" ");
        }

        return newStr.toString();
    }

    /**
     * Checks if a String is contained in quotes. "" or ''
     * @param s The String to check.
     * @return Whether or not the String is in quotes.
     */
    public static boolean isQuoted(String s) {
        return (s.startsWith("'") && s.endsWith("'")) || (s.startsWith("\"") && s.endsWith("\""));
    }

    /**
     * Adds double quotes around the String. ""
     * @param s The String to quote.
     * @return The quoted String.
     */
    public static String doubleQuote(String s) {
        return color(String.format("&f\"%s&f\"", s));
    }

    /**
     * Adds single quotes around the String. ''
     * @param s The String to quote.
     * @return The quoted String.
     */
    public static String singleQuote(String s) {
        return color(String.format("&f'%s&f'", s));
    }

    /**
     * Removes double or single quotes from the String. "" or ''
     * @param s The String to unquote.
     * @return The unquoted String.
     */
    public static String unquote(String s) {
        if (isQuoted(s))
            s = s.substring(1, s.length() - 1);

        return s;
    }
}
