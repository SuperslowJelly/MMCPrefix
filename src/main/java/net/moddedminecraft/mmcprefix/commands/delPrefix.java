package net.moddedminecraft.mmcprefix.commands;

import net.moddedminecraft.mmcprefix.Config;
import net.moddedminecraft.mmcprefix.Main;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.SubjectData;

import java.util.Optional;


public class delPrefix  implements CommandExecutor {

    private final Main plugin;
    public delPrefix(Main instance) {
        plugin = instance;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<Player> playerOP = args.getOne("player");

        if (playerOP.isPresent()) {
            if (src.hasPermission("mmcprefix.prefix.delete.other")) {
                Player player2 = playerOP.get();
                player2.getSubjectData().setOption(SubjectData.GLOBAL_CONTEXT, "prefix", null);
                plugin.sendMessage(src, Config.prefix + Config.messageDelPrefixOtherSucess
                .replace("{playername}", player2.getName()));
                plugin.runPrefixChangeCommands();
                return CommandResult.success();
            } else {
                throw new CommandException(plugin.fromLegacy("&cYou do not have permission to delete another users prefix."));
            }
        } else {
            if (src instanceof Player) {
                src.getSubjectData().setOption(SubjectData.GLOBAL_CONTEXT, "prefix", null);
                plugin.sendMessage(src, Config.prefix + Config.messageDelPrefixSelfSucess);
                plugin.runPrefixChangeCommands();
                return CommandResult.success();
            } else {
                throw new CommandException(plugin.fromLegacy("Only a player is able to delete their own prefix!"));
            }
        }
    }
}
