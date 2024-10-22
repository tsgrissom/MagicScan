package io.github.potterplus.api.misc;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;

@RequiredArgsConstructor
public enum DebugLevel {

    INFO(ChatColor.GRAY),

    SUCCESS(ChatColor.GREEN),

    DANGER(ChatColor.RED),

    WARNING(ChatColor.RED);

    @Getter
    @NonNull
    private final ChatColor prefixColor;
}
