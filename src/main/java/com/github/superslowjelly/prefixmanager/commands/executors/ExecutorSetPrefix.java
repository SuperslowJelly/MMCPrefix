package com.github.superslowjelly.prefixmanager.commands.executors;

import com.github.superslowjelly.prefixmanager.PrefixManager;
import com.github.superslowjelly.prefixmanager.commands.ACommandRegistry;
import com.github.superslowjelly.prefixmanager.utilities.Texts;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class ExecutorSetPrefix extends ACommandRegistry implements CommandExecutor {

    public ExecutorSetPrefix() {
        if (!this.aliases.contains("setprefix")) this.aliases.add("setprefix");
        if (!this.aliases.contains("sp")) this.aliases.add("sp");
        if (!alreadyContained(this)) COMMANDS.add(this);
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
            .description(Texts.of(this.getDescription()))
            .permission(this.getPermission())
            .executor(this)
            .arguments(
                GenericArguments.player(Text.of("player")),
                GenericArguments.string(Text.of("prefix name"))
            )
            .build();
    }

    @Override
    public void register() {}

    @Override
    public Class<?> getClazz() {
        return this.getClass();
    }

    @Override @NonnullByDefault
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player) args.getOne("player").get();
        String prefixName = (String) args.getOne("prefix name").get();

        if (!ExecutorSetPrefix.setPrefix(player, prefixName)) throw new CommandException(Texts.of("Prefix does not exist, please enter a valid prefix name!"));

        src.sendMessage(this.getResponseSuccess(player.getName(), PrefixManager.getConfig().PREFIX.getPrefixes().get(prefixName)));

        return CommandResult.success();
    }

    public static boolean setPrefix(Player player, String prefixName) {
        if (!PrefixManager.getConfig().PREFIX.getPrefixes().containsKey(prefixName)) return false;
        player.getSubjectData().setOption(SubjectData.GLOBAL_CONTEXT, "prefix", PrefixManager.getConfig().PREFIX.getPrefixes().get(prefixName));
        PrefixManager.getLogger().info("Set " + player.getName() + "'s prefix to " + prefixName + "!");
        return true;
    }

    @Setting(value = "enabled", comment = "Enable this command?")
    public boolean enabled = true;

    @Override
    public boolean getEnabled() {
        return PrefixManager.getConfig().COMMANDS.SETPREFIX.enabled;
    }

    @Setting(value = "description", comment = "The command's description.")
    public String description = "Sets the given player's prefix to the given prefix.";

    @Override
    public String getDescription() {
        return PrefixManager.getConfig().COMMANDS.SETPREFIX.description;
    }

    @Setting(value = "permission", comment = "Permission required to use this command.")
    public String permission = "prefixmanager.commands.setprefix.use";

    @Override
    public String getPermission() {
        return PrefixManager.getConfig().COMMANDS.SETPREFIX.permission;
    }

    @Setting(value = "aliases", comment = "Aliases to register for this command.")
    public List<String> aliases = new ArrayList<>();

    @Override
    public List<String> getAliases() {
        return PrefixManager.getConfig().COMMANDS.SETPREFIX.aliases;
    }

    @Setting(value = "response-success", comment = "Response to send to the command source on successful execution. Use %player% for player name, and %prefix% for the prefix.")
    public String responseSuccess = "&8&l[&c&lPrefixManager&8&l] &aSuccessfully set &e%player%'s &aprefix to &e%prefix%&a!";

    public Text getResponseSuccess(String player, String prefix) {
        return Texts.of(PrefixManager.getConfig().COMMANDS.SETPREFIX.responseSuccess.replace("%player%", player).replace("%prefix%", prefix));
    }
}
