package io.github.potterplus.api.command;

import io.github.potterplus.api.string.StringUtilities;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A wrapper class for the four arguments provided by Bukkit's CommandExecutor interface which simplifies and extends the functionality of checking, fetching, and operating on the elements of each command execution.
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CommandContext {

    /**
     * Returns a dummy CommandContext object with solely a CommandSender. Potentially useful for CommandBase's constant message sending methods.
     * @param sender The command sender.
     * @return The dummy CommandContext.
     */
    public static CommandContext dummy(CommandSender sender) {
        return new CommandContext(sender, null, null, null);
    }

    @Getter @NonNull
    private final CommandSender sender;

    @Getter @NonNull
    private final Command command;

    @Getter
    private final String label;

    @Getter
    private final String[] args;

    /**
     * Gets the sender's name (CONSOLE if it's console.)
     * @return The sender's name.
     */
    public String getSenderName() {
        return getSender().getName();
    }

    /**
     * Checks if the context's command sender is a player.
     * @return Whether the context's command sender is a player.
     */
    public boolean isPlayer() {
        return sender instanceof Player;
    }

    /**
     * Gets the context sender as a player.
     * @return The CommandSender as a Player.
     */
    public Player getPlayer() {
        if (isPlayer()) return (Player) getSender();
        else return null;
    }

    /**
     * Checks if the context's command sender was console.
     * @return Whether the context's command sender was console.
     */
    public boolean isConsole() {
        return sender instanceof ConsoleCommandSender;
    }

    /**
     * Gets the Bukkit ConsoleCommandSender.
     * @return Bukkit's console sender.
     */
    public ConsoleCommandSender getConsole() {
        return Bukkit.getConsoleSender();
    }

    /**
     * Attempts to resolve a player by their name.
     * @param player The name to resolve by.
     * @return An Optional wrapping the resolved Player. If the value is present, the Player is not null and is online.
     */
    public Optional<Player> resolveTarget(String player) {
        Player p = Bukkit.getPlayer(player);

        if (p == null || !p.isOnline()) {
            return Optional.empty();
        } else {
            return Optional.of(p);
        }
    }

    /**
     * Attempts to resolve a player by the first argument of the context.
     * @return An Optional wrapping the resolved Player. If the value is present, the Player is not null and is online.
     */
    public Optional<Player> resolveTarget() {
        if (!hasSub()) {
            return Optional.empty();
        }

        return resolveTarget(getSub());
    }

    /**
     * Checks if the command sender of this context has a permission.
     * @param permission The permission to check.
     * @return Whether the command sender of this context has the supplied permission.
     */
    public boolean hasPermission(Permission permission) {
        return sender.hasPermission(permission);
    }

    /**
     * Checks if the command sender of this context has a permission.
     * @param permission The permission to check.
     * @return Whether the command sender of this context has the supplied permission.
     */
    public boolean hasPermission(String permission) {
        return hasPermission(new Permission(permission));
    }

    /**
     * Checks if the first argument (sub-command) of this context is present.
     * @return Whether the context has a sub-command.
     */
    public boolean hasSub() {
        return hasArg(0);
    }

    /**
     * Fetches the first argument (sub-command) of this context.
     * @return The sub-command.
     */
    public String getSub() {
        if (args == null || args.length <= 0) return null;

        return args[0];
    }

    /**
     * Checks if this context has a specific argument.
     * @param arg The index to check for an argument.
     * @return Whether the context has the supplied argument.
     */
    public boolean hasArg(int arg) {
        if (args == null || args.length <= 0 || args.length < arg) return false;
        if (args.length >= arg && args[arg] == null) return false;

        return args[arg] != null;
    }

    /**
     * Fetches a specific argument from this context.
     * @param arg The index from which to fetch the argument.
     * @return The argument at the supplied index.
     */
    public String getArg(int arg) {
        if (args == null || args.length <= 0) return null;

        return args[arg];
    }

    public boolean hasArgs() {
        return args.length != 0;
    }

    public boolean hasNoArgs() {
        return args.length == 0;
    }

    /**
     * Concatenates a phrase with spaces between each argument beginning at the supplied index.
     * @param startingArgs The argument to start building the phrase at.
     * @return The phrase created, trimmed.
     */
    public String getMessage(int startingArgs) {
        if (args == null  || args.length < startingArgs) {
            throw new IndexOutOfBoundsException("Unchecked call of CommandContext#getMessage on the arguments. Verify all arguments are present.");
        }

        StringBuilder builder = new StringBuilder();

        for (int i = startingArgs; i < args.length; i++) {
            if (args[i].startsWith("--")) continue;
            builder.append(args[i]).append(" ");
        }

        return builder.toString().trim();
    }

    /**
     * Sends this context's command sender message(s).
     * @param messages The message(s) to send.
     */
    public void sendMessage(String... messages) {
        for (String str : messages) {
            sender.sendMessage(StringUtilities.color(str));
        }
    }

    /**
     * Sends this context's command sender messages.
     * @param messages The messages to send.
     */
    public void sendMessage(List<String> messages) {
        for (String str : messages) {
            sender.sendMessage(StringUtilities.color(str));
        }
    }

    /**
     * Sends a formatted and replaced message.
     * @param format The format to use.
     * @param replace What to replace.
     */
    public void sendMessage(String format, Map<String, String> replace) {
        sendMessage(StringUtilities.replace(format, replace));
    }

    /**
     * Sends a formatted and replaced multi-line message.
     * @param messages The message format to use.
     * @param replace What to replace.
     */
    public void sendMessage(List<String> messages, Map<String, String> replace) {
        sendMessage(StringUtilities.replace(messages, replace));
    }

    /**
     * Executes the command from console if this context's command sender was console. Otherwise, if the sender was a player they will perform the command.
     * @param command The command to execute/perform.
     */
    public void performCommand(String command) {
        if (sender instanceof ConsoleCommandSender) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        } else if (sender instanceof Player) {
            ((Player) sender).performCommand(command);
        }
    }

    /**
     * Performs the command as the CommandSender would, replacing everything in the map.
     * @param command The command format to replace, execute and perform.
     * @param replace The replacements to use.
     */
    public void performCommand(String command, Map<String, String> replace) {
        performCommand(StringUtilities.replace(command, replace));
    }

    /**
     * Dispatches a command to console, replacing everything in the map.
     * @param command The command format to replace and dispatch.
     * @param replace The replacements to use.
     */
    public void dispatchCommand(String command, Map<String, String> replace) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), StringUtilities.replace(command, replace));
    }

    /**
     * Checks if the supplied flag was used in the current context.
     * @param flag The flag to check for.
     * @return Whether the flag was present in the current context.
     */
    public boolean hasFlag(CommandFlag flag) {
        if (getArgs() == null || getArgs().length <= 0) return false;

        String longFlag = flag.getFullLongFlag();
        String shortFlag = flag.getFullShortFlag();

        for (String arg : getArgs()) {
            if (StringUtils.containsIgnoreCase(arg, longFlag) || StringUtils.containsIgnoreCase(arg, shortFlag)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Delegates the command logic to a SubCommand.
     * @param sub The SubCommand to delegate to.
     */
    public void delegate(CommandBase.SubCommand sub) {
        sub.execute(this);
    }
}
