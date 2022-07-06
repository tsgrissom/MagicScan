package io.github.potterplus.api.misc;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

/**
 * Represents MC-friendly formats for booleans.
 */
@RequiredArgsConstructor
public enum BooleanFormatter {

    TRUE_FALSE("true", "false"),

    YES_NO("yes", "no"),

    ENABLE_DISABLE("enable", "disable"),

    ENABLED_DISABLED("enabled", "disabled");

    public static String format(boolean bool, BooleanFormatter format, boolean capitalized, boolean colored, ChatColor colorTrue, ChatColor colorFalse) {
        String str = bool ? format.trueStr : format.falseStr;
        ChatColor color = bool ? colorTrue : colorFalse;

        str = capitalized ? StringUtils.capitalize(str) : str;
        str = colored ? color + str : str;

        return str;
    }

    public static String format(boolean bool, BooleanFormatter format, boolean capitalized, boolean colored) {
        return format(bool, format, capitalized, colored, ChatColor.GREEN, ChatColor.RED);
    }

    public static String format(boolean bool, BooleanFormatter format) {
        return format(bool, format, false, true);
    }

    @NonNull
    String trueStr, falseStr;

    public String format(boolean bool) {
        return BooleanFormatter.format(bool,this);
    }

    public String format(boolean bool, boolean capitalized, boolean colored) {
        return BooleanFormatter.format(bool, this, capitalized, colored);
    }
}
