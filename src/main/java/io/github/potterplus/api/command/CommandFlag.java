package io.github.potterplus.api.command;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommandFlag {

    public static final class UserInterfaceFlag extends CommandFlag {

        public UserInterfaceFlag() {
            super("ui", "u");
        }
    }

    @NonNull @Getter
    private final String longFlag, shortFlag;

    public String getFullLongFlag() {
        return String.format("--%s", getLongFlag());
    }

    public String getFullShortFlag() {
        return String.format("-%s", getShortFlag());
    }
}
