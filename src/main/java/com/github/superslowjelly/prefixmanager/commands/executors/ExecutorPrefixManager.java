package com.github.superslowjelly.prefixmanager.commands.executors;

import com.github.superslowjelly.prefixmanager.PrefixManager;
import com.github.superslowjelly.prefixmanager.commands.ACommandRegistry;
import com.github.superslowjelly.prefixmanager.utilities.Texts;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class ExecutorPrefixManager extends ACommandRegistry implements CommandExecutor {

    public ExecutorPrefixManager() {
        if (!this.aliases.contains("prefixmanager")) this.aliases.add("prefixmanager");
        if (!this.aliases.contains("pm")) this.aliases.add("pm");
        if (!alreadyContained(this)) COMMANDS.add(this);
    }

    @Override
    public void register() {
        final ArrayList<ACommandRegistry> COMMANDS = new ArrayList<>();

        ExecutorReload executorReload = PrefixManager.getConfig().COMMANDS.RELOAD;
        ExecutorAddPrefix executorAddPrefix = PrefixManager.getConfig().COMMANDS.ADDPREFIX;
        ExecutorSetPrefix executorSetPrefix = PrefixManager.getConfig().COMMANDS.SETPREFIX;

        if (executorReload.enabled) COMMANDS.add(executorReload);
        if (executorAddPrefix.enabled) COMMANDS.add(executorAddPrefix);
        if (executorSetPrefix.enabled) COMMANDS.add(executorSetPrefix);

        CommandSpec.Builder commandSpecBuilder = CommandSpec.builder()
            .description(Texts.of(this.getDescription()))
            .permission(this.getPermission())
            .executor(this);

        if (!COMMANDS.isEmpty()) {
            for (ACommandRegistry command : COMMANDS) {
                commandSpecBuilder.child(command.getSpec(), command.getAliases());
            }
        }

        Sponge.getCommandManager().register(PrefixManager.get(), commandSpecBuilder.build(), this.getAliases());
    }

    @Override
    public CommandSpec getSpec() {
        return null;
    }

    @Override
    public Class<?> getClazz() {
        return this.getClass();
    }

    @Override @NonnullByDefault
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        PaginationList.builder()
            .title(Texts.of(PrefixManager.getConfig().COMMANDS.PREFIXMANAGER.RESPONSE.title))
            .padding(Texts.of(PrefixManager.getConfig().COMMANDS.PREFIXMANAGER.RESPONSE.padding))
            .contents(PrefixManager.getConfig().COMMANDS.PREFIXMANAGER.RESPONSE.getDescriptions(ACommandRegistry.COMMANDS, src))
            .build()
            .sendTo(src);
        return CommandResult.success();
    }

    @Setting(value = "enabled", comment = "Enable this command?")
    public boolean enabled = true;

    @Override
    public boolean getEnabled() {
        return PrefixManager.getConfig().COMMANDS.PREFIXMANAGER.enabled;
    }

    @Setting(value = "description", comment = "The command's description.")
    public String description = "The main PrefixManager command.";

    @Override
    public String getDescription() {
        return PrefixManager.getConfig().COMMANDS.PREFIXMANAGER.description;
    }

    @Setting(value = "permission", comment = "Permission required to use this command.")
    public String permission = "prefixmanager.commands.prefixmanager.use";

    @Override
    public String getPermission() {
        return PrefixManager.getConfig().COMMANDS.PREFIXMANAGER.permission;
    }

    @Setting(value = "aliases", comment = "Aliases to register for this command.")
    public List<String> aliases = new ArrayList<>();

    @Override
    public List<String> getAliases() {
        return PrefixManager.getConfig().COMMANDS.PREFIXMANAGER.aliases;
    }

    @Setting(value = "response", comment = "Configuration for the command's response.")
    public final ExecutorRedeemablesResponse RESPONSE = new ExecutorRedeemablesResponse();

    @ConfigSerializable
    public static class ExecutorRedeemablesResponse {

        public List<Text> getDescriptions(List<ACommandRegistry> commands, CommandSource src) {
            ArrayList<Text> response = new ArrayList<>();
            for (ACommandRegistry command : commands) {
                if (command.getEnabled() &&
                    !command.getClazz().equals(PrefixManager.getConfig().COMMANDS.PREFIXMANAGER.getClazz()) &&
                    !command.getClazz().equals(PrefixManager.getConfig().COMMANDS.PREFIXLIST.getClazz()) &&
                    src.hasPermission(command.getPermission())
                ) {
                    response.add(Texts.of(this.description
                        .replace("%command%", "/" + PrefixManager.getConfig().COMMANDS.PREFIXMANAGER.getAliases().get(0) + " " + command.getAliases().get(0))
                        .replace("%description%", command.getDescription())
                    ));
                }
            }
            return response;
        }

        @Setting(value = "padding", comment = "Padding of the help page.")
        public String padding = "&8&m-";

        @Setting(value = "title", comment = "Title of the help page.")
        public String title = "&c&lPrefixManager";

        @Setting(value = "description", comment = "Formatting of each command's details. Use %command% for the command itself, and %description% for the command's written description.")
        public String description = " &7* &c%command% &7- %description%";
    }
}
