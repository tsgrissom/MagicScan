package io.github.potterplus.api.string;

import lombok.Getter;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HoverMessage {

    @Getter
    private boolean autoscroll;
    private List<String> message;
    private List<String> hoverText;

    public static HoverMessage compose(String... initial) {
        return new HoverMessage(initial);
    }

    public HoverMessage() {
        this.autoscroll = true;
        this.message = new ArrayList<>();
        this.hoverText = new ArrayList<>();
    }

    public HoverMessage(String... initial) {
        this();

        message.addAll(Arrays.asList(initial));
    }

    public HoverMessage message(List<String> message) {
        this.message = message;

        return this;
    }

    public HoverMessage hoverText(List<String> hoverText) {
        this.hoverText = hoverText;

        return this;
    }

    public HoverMessage withHover(String text) {
        this.hoverText.add(text);

        return this;
    }

    public HoverMessage withText(String text) {
        this.message.add(text);

        return this;
    }

    public HoverMessage autoscroll(boolean bool) {
        this.autoscroll = bool;

        return this;
    }

    public void send(CommandSender sender) {
        if (sender instanceof Player) {
            send((Player) sender);
        } else if (sender instanceof ConsoleCommandSender) {
            sendUnfolded(sender);
        } else {
            Bukkit.getLogger().severe("Cannot send HoverMessage to type other than Player or Console!");
        }
    }

    public void send(Player to) {
        for (String m : message) {
            m = StringUtilities.color(m);

            TextComponent msg = new TextComponent(m);
            HoverEvent e = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(StringUtilities.color(hoverText.get(0) + "\n")));

            List<String> sub = hoverText.subList(1, hoverText.size());

            for (String t : sub) {
                t = StringUtilities.color(t);

                if (autoscroll) {
                    t += "\n";
                }

                e.addContent(new Text(t));
            }

            msg.setHoverEvent(e);

            to.spigot().sendMessage(msg);
        }
    }

    public List<String> getUnfolded() {
        List<String> list = new ArrayList<>();

        for (String m : message) {
            m = StringUtilities.color(m);
            list.add(m);
        }

        list.addAll(message.stream().map(StringUtilities::color).collect(Collectors.toList()));
        list.addAll(hoverText.stream().map(StringUtilities::color).collect(Collectors.toList()));

        return list;
    }

    public void sendUnfolded(CommandSender to) {
        getUnfolded().forEach(to::sendMessage);
    }
}