package com.github.superslowjelly.prefixmanager.commands.executors;

import com.github.superslowjelly.prefixmanager.PrefixManager;
import com.github.superslowjelly.prefixmanager.commands.ACommandRegistry;
import com.github.superslowjelly.prefixmanager.utilities.TaskHelper;
import com.github.superslowjelly.prefixmanager.utilities.Texts;
import com.github.superslowjelly.prefixmanager.utilities.Utils;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@ConfigSerializable
public class ExecutorPrefixList extends ACommandRegistry implements CommandExecutor {

    public ExecutorPrefixList() {
        if (!this.aliases.contains("prefixlist")) this.aliases.add("prefixlist");
        if (!this.aliases.contains("pl")) this.aliases.add("pl");
        if (!alreadyContained(this)) COMMANDS.add(this);
    }

    @Override
    public void register() {
        Sponge.getCommandManager().register(
            PrefixManager.get(),
            CommandSpec.builder()
                .description(Texts.of(this.getDescription()))
                .permission(this.getPermission())
                .executor(this)
                .build(),
            this.getAliases()
        );
    }

    @Override
    public CommandSpec getSpec() {
        return null;
    }

    @Override
    public Class<?> getClazz() {
        return this.getClass();
    }

    private final List<String> COOLDOWN_PLAYERS = new ArrayList<>();

    @Override @NonnullByDefault
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            List<Text> contents = new ArrayList<>();
            Player player = (Player) src;

            for (Map.Entry<String, String> entry : PrefixManager.getConfig().PREFIX.getPrefixes().entrySet()) {
                Text.Builder response = Text.builder();
                String prefixName = entry.getKey();
                String prefix = entry.getValue();
                if (player.hasPermission("prefixmanager.prefix." + prefixName)) {
                    response.append(this.RESPONSE.getDescription(Utils.getNick(player), prefix));
                    if (!this.RESPONSE.isHoverMessageEmpty()) response.onHover(TextActions.showText(this.RESPONSE.getHoverMessage(Utils.getNick(player), prefix)));
                    response.onClick(TextActions.executeCallback(processPrefix(player, prefixName)));
                    contents.add(response.build());
                }
            }

            PaginationList.Builder paginationListBuilder = PaginationList.builder()
                .title(this.RESPONSE.getTitle())
                .padding(this.RESPONSE.getPadding())
                .contents(contents);
            if (!this.RESPONSE.isHeaderEmpty()) paginationListBuilder.header(this.RESPONSE.getHeader());
            paginationListBuilder.sendTo(player);

            return CommandResult.success();
        } else {
            throw new CommandException(Texts.of("This command can only be run by players!"));
        }
    }

    private Consumer<CommandSource> processPrefix(Player player, String prefixName) {
        return consumer -> {
            if (COOLDOWN_PLAYERS.contains(player.getName()) && this.getCooldown() >= 1 && !consumer.hasPermission(this.permissionCooldownBypass)) {
                consumer.sendMessage(this.getCooldownTriggerResponse());
            } else {
                ExecutorSetPrefix.setPrefix(player, prefixName);
                consumer.sendMessage(this.getSetPrefixResponse(prefixName));
                if (!consumer.hasPermission(this.getPermissionCooldownBypass())) {
                    COOLDOWN_PLAYERS.add(player.getName());
                    TaskHelper.scheduleASync(this.getCooldown(), TimeUnit.MINUTES, () -> this.COOLDOWN_PLAYERS.remove(player.getName()));
                }
            }
        };
    }

    @Setting(value = "enabled", comment = "Enable this command?")
    public boolean enabled = true;

    @Override
    public boolean getEnabled() {
        return PrefixManager.getConfig().COMMANDS.PREFIXLIST.enabled;
    }

    @Setting(value = "description", comment = "The command's description.")
    public String description = "The main PrefixManager command.";

    @Override
    public String getDescription() {
        return PrefixManager.getConfig().COMMANDS.PREFIXLIST.description;
    }

    @Setting(value = "permission", comment = "Permission required to use this command.")
    public String permission = "prefixmanager.commands.prefixlist.use";

    @Override
    public String getPermission() {
        return PrefixManager.getConfig().COMMANDS.PREFIXLIST.permission;
    }

    @Setting(value = "aliases", comment = "Aliases to register for this command.")
    public List<String> aliases = new ArrayList<>();

    @Override
    public List<String> getAliases() {
        return PrefixManager.getConfig().COMMANDS.PREFIXLIST.aliases;
    }

    @Setting(value = "cooldown", comment = "Set prefix cooldown.")
    public int cooldown = 1;

    public int getCooldown() {
        return PrefixManager.getConfig().COMMANDS.PREFIXLIST.cooldown;
    }

    @Setting(value = "permission-cooldown-bypass", comment = "Permission required to bypass the set prefix cooldown.")
    public String permissionCooldownBypass = "prefixmanager.cooldown.bypass";

    public String getPermissionCooldownBypass() {
        return PrefixManager.getConfig().COMMANDS.PREFIXLIST.permissionCooldownBypass;
    }

    @Setting(value = "cooldown-trigger-response", comment = "Response to send when the cooldown is triggered, use %cooldown% for cooldown value in minutes.")
    public String cooldownTriggerResponse = "&8&l[&c&lPrefixManager&8&l] &cYou must wait &e%cooldown% &cto set your prefix again!";

    public Text getCooldownTriggerResponse() {
        return Texts.of(PrefixManager.getConfig().COMMANDS.PREFIXLIST.cooldownTriggerResponse.replace("%cooldown%", (this.getCooldown() == 1 ? "1 minute" : this.getCooldown() + " minutes")));
    }

    @Setting(value = "set-prefix-response", comment = "Response to send when the user's prefix is set, use %prefix% for the prefix itself.")
    public String setPrefixResponse = "&8&l[&c&lPrefixManager&8&l] &aSuccessfuly set your prefix to &e%prefix%&a!";

    public Text getSetPrefixResponse(String prefix) {
        return Texts.of(PrefixManager.getConfig().COMMANDS.PREFIXLIST.setPrefixResponse.replace("%prefix%", prefix));
    }

    @Setting(value = "response", comment = "Configuration for the command's response.")
    public final ExecutorRedeemablesResponse RESPONSE = new ExecutorRedeemablesResponse();

    @ConfigSerializable
    public static class ExecutorRedeemablesResponse {

        @Setting(value = "padding", comment = "Padding of the help page.")
        public String padding = "&8&m-";

        public Text getPadding() {
            return Texts.of(PrefixManager.getConfig().COMMANDS.PREFIXLIST.RESPONSE.padding);
        }

        @Setting(value = "title", comment = "Title of the prefix list page.")
        public String title = "&c&lPrefix List";

        public Text getTitle() {
            return Texts.of(PrefixManager.getConfig().COMMANDS.PREFIXLIST.RESPONSE.title);
        }

        @Setting(value = "header", comment = "Header of the prefix list page.")
        public String header = "";

        public boolean isHeaderEmpty() {
            return PrefixManager.getConfig().COMMANDS.PREFIXLIST.RESPONSE.header.isEmpty();
        }

        public Text getHeader() {
            return Texts.of(PrefixManager.getConfig().COMMANDS.PREFIXLIST.RESPONSE.header);
        }

        @Setting(value = "description", comment = "Formatting of each prefix's details. Use %player% for the player name, and %prefix% for the prefix itself.")
        public String description = " &7- &r%prefix% &r%player%";

        public Text getDescription(String player, String prefix) {
            return Texts.of(PrefixManager.getConfig().COMMANDS.PREFIXLIST.RESPONSE.description.replace("%player%", player).replace("%prefix%", prefix));
        }

        @Setting(value = "hover-message", comment = "Text to show when hovering over each prefix, use %prefix% for the prefix, and %player% for the player name.")
        public String hoverMessage = "&eSet your current prefix to: &r%prefix% %player%";

        public boolean isHoverMessageEmpty() {
            return PrefixManager.getConfig().COMMANDS.PREFIXLIST.RESPONSE.hoverMessage.isEmpty();
        }

        public Text getHoverMessage(String player, String prefix) {
            return Texts.of(PrefixManager.getConfig().COMMANDS.PREFIXLIST.RESPONSE.hoverMessage.replace("%player%", player).replace("%prefix%", prefix));
        }
    }
}
