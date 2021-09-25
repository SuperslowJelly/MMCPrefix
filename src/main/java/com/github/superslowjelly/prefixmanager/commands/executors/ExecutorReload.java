package com.github.superslowjelly.prefixmanager.commands.executors;

import com.github.superslowjelly.prefixmanager.PrefixManager;
import com.github.superslowjelly.prefixmanager.commands.ACommandRegistry;
import com.github.superslowjelly.prefixmanager.utilities.Texts;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class ExecutorReload extends ACommandRegistry implements CommandExecutor {

    public ExecutorReload() {
        if (!this.aliases.contains("reload")) this.aliases.add("reload");
        if (!this.aliases.contains("reboot")) this.aliases.add("reboot");
        if (!alreadyContained(this)) COMMANDS.add(this);
    }

    private Text getResponseSuccess(String name) {
        return Texts.of(PrefixManager.getConfig().COMMANDS.RELOAD.responseSuccess.replace("%player%", name));
    }

    private Text getResponseFailure(String name) {
        return Texts.of(PrefixManager.getConfig().COMMANDS.RELOAD.responseFailure.replace("%player%", name));
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
            .description(Texts.of(this.getDescription()))
            .permission(this.getPermission())
            .executor(this)
            .build();
    }

    @Override
    public void register() {
    }

    @Override
    public Class<?> getClazz() {
        return this.getClass();
    }

    @Override @NonnullByDefault
    public CommandResult execute(CommandSource src, CommandContext args) {
        if (PrefixManager.reload()) {
            src.sendMessage(this.getResponseSuccess(src.getName()));
            return CommandResult.success();
        } else {
            src.sendMessage(this.getResponseFailure(src.getName()));
            return null;
        }
    }

    @Setting(value = "enabled", comment = "Enable this command?")
    public boolean enabled = true;

    @Override
    public boolean getEnabled() {
        return PrefixManager.getConfig().COMMANDS.RELOAD.enabled;
    }

    @Setting(value = "description", comment = "The command's description.")
    public String description = "Reloads the plugin.";

    @Override
    public String getDescription() {
        return PrefixManager.getConfig().COMMANDS.RELOAD.description;
    }

    @Setting(value = "permission", comment = "Permission required to use this command.")
    public String permission = "prefixmanager.commands.reload.use";

    @Override
    public String getPermission() {
        return PrefixManager.getConfig().COMMANDS.RELOAD.permission;
    }

    @Setting(value = "aliases", comment = "Aliases to register for this command.")
    public List<String> aliases = new ArrayList<>();

    @Override
    public List<String> getAliases() {
        return PrefixManager.getConfig().COMMANDS.RELOAD.aliases;
    }

    @Setting(value = "response-success", comment = "Response to send to the command source on successful reload. Use %player% for player name.")
    public String responseSuccess = "&8&l[&c&lPrefixManager&8&l] &aSuccessfully reloaded the plugin.";

    @Setting(value = "response-failure", comment = "Response to send to the command source on failed reload. Use %player% for player name.")
    public String responseFailure = "&8&l[&c&lPrefixManager&8&l] &cFailed to reload the plugin, check console for errors!";
}
