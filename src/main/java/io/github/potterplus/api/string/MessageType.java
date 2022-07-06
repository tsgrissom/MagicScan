package io.github.potterplus.api.string;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

@RequiredArgsConstructor
public enum MessageType {

    ACTION_BAR("a:"),

    TITLE("t:");

    @NonNull @Getter
    private final String prefix;

    private void sendActionBar(CommandSender to, String message) {
        if (to instanceof Player) {
            ((Player) to).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
        } else  if (to instanceof ConsoleCommandSender) {
            to.sendMessage(message);
        }
    }

    private void sendTitle(CommandSender to, String title, String subtitle) {
        if (to instanceof Player) {
            ((Player) to).sendTitle(title, subtitle, 40, 100, 40);
        } else if (to instanceof ConsoleCommandSender) {
            to.sendMessage(title);
            to.sendMessage(subtitle);
        }
    }

    public void send(CommandSender to, String message) {
        switch (this) {
            case ACTION_BAR: {
                sendActionBar(to, message);
            }
            case TITLE: {
                if (message.contains(Pattern.quote("\n"))) {
                    String[] split = message.split("\n");
                    sendTitle(to, split[0], split[1]);
                }
            }
        }
    }
}
