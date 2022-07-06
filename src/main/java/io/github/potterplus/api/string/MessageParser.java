package io.github.potterplus.api.string;

import lombok.NonNull;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

public class MessageParser {

    @NonNull
    private final String message;
    private MessageType type = null;

    @Override
    public String toString() {
        return type != null ? message.substring(2) : message;
    }

    public MessageParser(String message) {
        this.message = StringUtilities.color(message);

        for (MessageType type : MessageType.values()) {
            if (StringUtils.startsWithIgnoreCase(message, type.getPrefix())) {
                this.type = type;
            }
        }
    }

    public void send(CommandSender to) {
        if (type != null) {
            type.send(to, toString());
        } else {
            to.sendMessage(toString());
        }
    }
}
