package net.moddedminecraft.mmcprefix.commands;

import net.moddedminecraft.mmcprefix.Config;
import net.moddedminecraft.mmcprefix.Main;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Text.Builder;
import org.spongepowered.api.text.action.TextActions;

import java.util.ArrayList;
import java.util.List;

public class prefixList implements CommandExecutor {

    private final Main plugin;
    public prefixList(Main instance) {
        plugin = instance;
    }
    private int index = 1;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();

        int total = Config.getConfig().getNode("list").getChildrenMap().keySet().size();
        List<Text> contents = new ArrayList<>();

        if (src instanceof Player) {
            Player player = (Player) src;
            for (index = 1; index <= total; index++) {
                String indstr = String.valueOf(index);
                Builder send = Text.builder();
                String prefix = Config.getConfig().getNode("list", indstr, "prefix").getString();
                if (player.hasPermission("mmcprefix.list." + Config.getConfig().getNode("list", indstr, "permission"))) {
                    send.append(plugin.fromLegacy("&3" + indstr + "&f: " + prefix));
                    send.onHover(TextActions.showText(plugin.fromLegacy("Set your current prefix to: " + prefix + player.getName())));
                    send.onClick(TextActions.runCommand("/setprefix " + prefix + " " + player.getName()));

                    contents.add(send.build());
                }
            }

            if (index >= total) {
                index = 1;
            }

            paginationService.builder()
                    .title(plugin.fromLegacy(Config.prefixListTitle))
                    .contents(contents)
                    .padding(Text.of("="))
                    .sendTo(player);

            return CommandResult.success();
        } else {
            throw new CommandException(plugin.fromLegacy("Only a player is able to get their own prefix's"));
        }
    }
}
