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
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class ExecutorAddPrefix extends ACommandRegistry implements CommandExecutor {

    public ExecutorAddPrefix() {
        if (!this.aliases.contains("addprefix")) this.aliases.add("addprefix");
        if (!this.aliases.contains("ap")) this.aliases.add("ap");
        if (!alreadyContained(this)) COMMANDS.add(this);
    }

    private Text getResponseSuccess(String player, String prefix) {
        return Texts.of(PrefixManager.getConfig().COMMANDS.ADDPREFIX.responseSuccess.replace("%player%", player).replace("%prefix%", prefix));
    }

    private Text getResponsePlayer(String player, String prefix) {
        return Texts.of(PrefixManager.getConfig().COMMANDS.ADDPREFIX.responsePlayer.replace("%player%", player).replace("%prefix%", prefix));
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
        String permission = (String) args.getOne("prefix name").get();

        if (!PrefixManager.getConfig().PREFIX.getPrefixes().containsKey(permission)) throw new CommandException(Texts.of("Prefix does not exist, please enter a valid prefix name!"));

        player.getSubjectData().setPermission(SubjectData.GLOBAL_CONTEXT, "prefixmanager.prefix." + permission, Tristate.TRUE);
        src.sendMessage(this.getResponseSuccess(player.getName(), PrefixManager.getConfig().PREFIX.getPrefixes().get(permission)));
        if (player.isOnline()) player.sendMessage(this.getResponsePlayer(player.getName(), PrefixManager.getConfig().PREFIX.getPrefixes().get(permission)));

        return CommandResult.success();
    }

    @Setting(value = "enabled", comment = "Enable this command?")
    public boolean enabled = true;

    @Override
    public boolean getEnabled() {
        return PrefixManager.getConfig().COMMANDS.ADDPREFIX.enabled;
    }

    @Setting(value = "description", comment = "The command's description.")
    public String description = "Gives the given player the permission to use the given prefix in /prefixlist.";

    @Override
    public String getDescription() {
        return PrefixManager.getConfig().COMMANDS.ADDPREFIX.description;
    }

    @Setting(value = "permission", comment = "Permission required to use this command.")
    public String permission = "prefixmanager.commands.addprefix.use";

    @Override
    public String getPermission() {
        return PrefixManager.getConfig().COMMANDS.ADDPREFIX.permission;
    }

    @Setting(value = "aliases", comment = "Aliases to register for this command.")
    public List<String> aliases = new ArrayList<>();

    @Override
    public List<String> getAliases() {
        return PrefixManager.getConfig().COMMANDS.ADDPREFIX.aliases;
    }

    @Setting(value = "response-success", comment = "Response to send to the command source on successful execution. Use %player% for player name, and %prefix% for the prefix.")
    public String responseSuccess = "&8&l[&c&lPrefixManager&8&l] &aSuccessfully gave &e%player% &athe &e%prefix% &aprefix!.";

    @Setting(value = "response-player", comment = "Response to send to the player on successful execution. Use %player% for player name, and %prefix% for the prefix.")
    public String responsePlayer = "&8&l[&c&lPrefixManager&8&l] &aYou received the &e%prefix% &aprefix, use &e/prefixlist &ato check it out!";
}
