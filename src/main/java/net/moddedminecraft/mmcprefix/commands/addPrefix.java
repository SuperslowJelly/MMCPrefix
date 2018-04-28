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
import org.spongepowered.api.util.Tristate;

import java.util.Optional;

public class addPrefix  implements CommandExecutor {

    private final Main plugin;
    public addPrefix(Main instance) {
        plugin = instance;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<String> permOP = args.getOne("permission");
        Optional<Player> playerOP = args.getOne("player");

        if (playerOP.isPresent() & permOP.isPresent()) {
            Player player = playerOP.get();
            player.getSubjectData().setPermission(SubjectData.GLOBAL_CONTEXT, "mmcprefix.list."+ permOP.get(), Tristate.TRUE);
            plugin.sendMessage(src, Config.prefix + Config.messageAddPrefixSucess
                    .replace("{playername}", player.getName())
                    .replace("{permission}", "mmcprefix.list." + permOP.get()));
            plugin.runPrefixChangeCommands();
            return CommandResult.success();
        } else {
            throw new CommandException(plugin.fromLegacy("Usage: /addprefix <permission from config> <name>"));
        }

    }
}
