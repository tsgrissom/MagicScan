package io.github.potterplus.magicscan.command.sub;

import io.github.potterplus.api.command.CommandBase;
import io.github.potterplus.api.command.CommandContext;
import io.github.potterplus.api.command.CommandFlag;
import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.task.QuickScanTask;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * TODO Write docs
 */
@RequiredArgsConstructor
public class QuickScanSubCommand extends CommandBase.SubCommand {

    @NonNull
    private final MagicScanController controller;

    @Override
    public void execute(CommandContext context) {
        CommandFlag uiFlag = new CommandFlag.UserInterfaceFlag();

        if (context.isPlayer()) {
            new QuickScanTask(controller, context.getPlayer()).run();
        } else if (context.isConsole()) {
            if (context.hasFlag(uiFlag)) {
                controller.sendMessage(context, "no_gui_console");
            } else {
                new QuickScanTask(controller, context.getConsole()).run();
            }
        }
    }
}
