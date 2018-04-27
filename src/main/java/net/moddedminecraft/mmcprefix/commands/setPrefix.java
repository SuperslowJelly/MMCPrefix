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

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class setPrefix implements CommandExecutor {

    private final Main plugin;
    public setPrefix(Main instance) {
        plugin = instance;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<Player> playerOP = args.getOne("player");
        Optional<String> prefixOP = args.<String>getOne("prefix");
        Optional<String> prefixListOP = args.<String>getOne("custom");
        String prefix;
        int length;

        Pattern prefixColorRegex = Pattern.compile("(&([a-f0-9]))", Pattern.CASE_INSENSITIVE);
        Pattern prefixFormatRegex = Pattern.compile("(&([k-o]))", Pattern.CASE_INSENSITIVE);

        if (prefixOP.isPresent() && (src.hasPermission("mmcprefix.prefix.set.self") || src.hasPermission("mmcprefix.prefix.set.other"))) {
            prefix = prefixOP.get();
            length = prefix.replaceAll("(&([a-f0-9]))", "").length();
        } else {
            if (src.hasPermission("mmcprefix.prefix.set.other")) {
                throw new CommandException(plugin.fromLegacy("&cUsage: /setprefix <prefix> [<player>]"));
            } else {
                throw new CommandException(plugin.fromLegacy("&cUsage: /setprefix <prefix>"));
            }
        }

        if (playerOP.isPresent()) {
            if (src.hasPermission("mmcprefix.prefix.set.other")) {
                Player player2 = playerOP.get();
                if (player2.hasPermission("mmcprefix.prefix.protected") && src instanceof Player) {
                    throw new CommandException(plugin.fromLegacy("&4The prefix of the specified player cannot be changed."));
                } else {
                    if (prefixListOP.isPresent()) {
                        if (src.hasPermission("mmcprefix.prefix.set.list")) {
                            if (prefixListOP.get().equalsIgnoreCase("custom")) {
                                player2.getSubjectData().setOption(SubjectData.GLOBAL_CONTEXT, "prefix", prefix);
                                plugin.sendMessage(src, Config.prefix + "&3Prefix Set for &6" + player2.getName() + "&3: " + prefix);
                                plugin.runPrefixChangeCommands();
                            }
                        } else {
                            throw new CommandException(plugin.fromLegacy("&cYou do not permission to use this command."));
                        }
                    } else {
                        player2.getSubjectData().setOption(SubjectData.GLOBAL_CONTEXT, "prefix", Config.prefixFormat.replace("%prefix%", prefix));
                        plugin.sendMessage(src, Config.prefix + "&3Prefix Set for &6" + player2.getName() + "&3: " + Config.prefixFormat.replace("%prefix%", prefix));
                        plugin.runPrefixChangeCommands();
                    }
                    return CommandResult.success();
                }
            } else {
                throw new CommandException(plugin.fromLegacy("&cYou do not permission to use this command."));
            }
        } else {
            if (src instanceof Player) {
                if (length <= Config.prefixMaxCharacterLimit || src.hasPermission("mmcprefix.prefix.bypass")) {
                    if (!src.hasPermission("mmcprefix.prefix.staff") && (checkPrefixBlacklist(prefix))) {
                        throw new CommandException(plugin.fromLegacy("&4You cannot have this prefix."));
                    } else {
                        if (!src.hasPermission("mmcprefix.prefix.color") && prefixColorRegex.matcher(prefix).find() || !src.hasPermission("mmcprefix.prefix.format") && prefixFormatRegex.matcher(prefix).find()) {
                            throw new CommandException(plugin.fromLegacy(Config.prefix + "&4You do not have the permission to use chat codes in your prefix."));
                        } else {
                            src.getSubjectData().setOption(SubjectData.GLOBAL_CONTEXT, "prefix", Config.prefixFormat.replace("%prefix%", prefix));
                            plugin.sendMessage(src, Config.prefix + "&3Prefix Set to: &f" + Config.prefixFormat.replace("%prefix%", prefix));
                            plugin.runPrefixChangeCommands();
                            return CommandResult.success();
                        }
                    }
                } else {
                    throw new CommandException(plugin.fromLegacy("&4You cannot have a prefix longer than " + Config.prefixMaxCharacterLimit + " characters"));
                }
            } else {
                throw new CommandException(plugin.fromLegacy("Only a player is able to set their own prefix!"));
            }
        }
    }

    public boolean checkPrefixBlacklist(String prefix) {
        List<String> prefix1 = Config.prefixBlacklist;
        String[] prefixsplit = prefix.split(" ");
        String prefixconvert = prefixsplit[0];
        for (String prefix2 : prefix1) {
            if (prefixconvert.toLowerCase().contains(prefix2)) {
                return true;
            }
        }
        return false;
    }
}
