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
import java.util.function.Consumer;

public class prefixList implements CommandExecutor {

    private final Main plugin;
    public prefixList(Main instance) {
        plugin = instance;
    }
    private int index = 1;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();

        int total = Config.getConfig().getNode("list", "content").getChildrenMap().keySet().size();
        List<Text> contents = new ArrayList<>();

        if (src instanceof Player) {
            Player player = (Player) src;
            for (index = 1; index <= total; index++) {
                String indstr = String.valueOf(index);
                Builder send = Text.builder();
                String permission = Config.getConfig().getNode("list", "content", indstr, "permission").getString();
                String prefix = Config.getConfig().getNode("list", "content", indstr, "prefix").getString();
                if (player.hasPermission("mmcprefix.list." + permission) || permission.isEmpty()) {
                    send.append(plugin.fromLegacy("&3- &f" + prefix));
                    if (!Config.prefixListHover.isEmpty()) {
                        send.onHover(TextActions.showText(plugin.fromLegacy(Config.prefixListHover.replace("{prefix}", prefix).replace("{playername}", player.getName()))));
                    }
                    send.onClick(TextActions.executeCallback(processPrefix(prefix , player.getName())));
                    contents.add(send.build());
                }
            }

            if (index >= total) {
                index = 1;
            }

            if (Config.prefixListHeader.isEmpty()) {
                paginationService.builder()
                        .title(plugin.fromLegacy(Config.prefixListTitle))
                        .contents(contents)
                        .padding(Text.of("="))
                        .sendTo(player);

            } else {
                paginationService.builder()
                        .title(plugin.fromLegacy(Config.prefixListTitle))
                        .header(plugin.fromLegacy(Config.prefixListHeader))
                        .contents(contents)
                        .padding(Text.of("="))
                        .sendTo(player);
            }

            return CommandResult.success();
        } else {
            throw new CommandException(plugin.fromLegacy("Only a player is able to get their own prefix's"));
        }
    }

    private Consumer<CommandSource> processPrefix(String prefix, String name) {
        return consumer -> {
            Sponge.getCommandManager().process(Sponge.getServer().getConsole(), "setprefix " + prefix + " " + name + " custom");
            plugin.sendMessage(consumer, "&f[&6MMCPrefix&f] &3Prefix Set to: &f" + prefix);
        };
    }
}
