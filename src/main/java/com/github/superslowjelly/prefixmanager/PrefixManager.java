package com.github.superslowjelly.prefixmanager;

import com.google.inject.Inject;
import com.github.superslowjelly.prefixmanager.commands.addPrefix;
import com.github.superslowjelly.prefixmanager.commands.delPrefix;
import com.github.superslowjelly.prefixmanager.commands.prefixList;
import com.github.superslowjelly.prefixmanager.commands.setPrefix;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.bstats.sponge.Metrics2;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Plugin(id = "mmcprefix", name = "mmcprefix", version = "1.2.3", description = "A simple and easy to use custom prefix manager")
public class PrefixManager {

    @Inject
    public Logger logger;

    @Inject
    private Metrics2 metrics;

    @Inject
    @DefaultConfig(sharedRoot = false)
    public Path defaultConf;

    @Inject
    @DefaultConfig(sharedRoot = false)
    public File defaultConfFile;

    private Config config;

    private CommandManager cmdManager = Sponge.getCommandManager();

    @Listener
    public void Init(GameInitializationEvent event) throws IOException, ObjectMappingException {
        this.config = new Config(this);
        loadCommands();
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) throws IOException {
        logger.info("MMCPrefix Loaded");
    }

    public void onServerStop(GameStoppingEvent event) throws IOException {
        logger.info("MMCPrefix Disabled");
    }

    @Listener
    public void onPluginReload(GameReloadEvent event) throws IOException, ObjectMappingException {
        this.config = new Config(this);
    }

    private void loadCommands() {

        // /prefixlist
        CommandSpec prefixlist = CommandSpec.builder()
                .description(Text.of("List all avaliable prefixes"))
                .permission("mmcprefix.prefix.list")
                .executor(new prefixList(this))
                .build();

        // /delprefix (player)
        CommandSpec delprefix = CommandSpec.builder()
                .description(Text.of("Delete the custom prefix for yourself"))
                .permission("mmcprefix.prefix.delete.self")
                .arguments(
                        GenericArguments.optional(GenericArguments.player(Text.of("player")))
                )
                .executor(new delPrefix(this))
                .build();

        // /setprefix prefix (player)
        CommandSpec setprefix = CommandSpec.builder()
                .description(Text.of("Set a prefix for yourself"))
                .permission("mmcprefix.prefix.set.self")
                .arguments(
                        GenericArguments.optional(GenericArguments.string(Text.of("prefix"))),
                        GenericArguments.optional(GenericArguments.player(Text.of("player"))),
                        GenericArguments.optional(GenericArguments.string(Text.of("custom")))
                )
                .executor(new setPrefix(this))
                .build();

        // /addprefix permission player
        CommandSpec addprefix = CommandSpec.builder()
                .description(Text.of("Give a player the permission to use a prefix in /prefixlist"))
                .permission("mmcprefix.prefix.addprefix")
                .arguments(
                        GenericArguments.optional(GenericArguments.string(Text.of("permission"))),
                        GenericArguments.optional(GenericArguments.player(Text.of("player")))
                )
                .executor(new addPrefix(this))
                .build();


        cmdManager.register(this, prefixlist, "prefixlist");
        cmdManager.register(this, delprefix, "delprefix");
        cmdManager.register(this, setprefix, "setprefix");
        cmdManager.register(this, addprefix, "addprefix");
    }

    public void runPrefixChangeCommands() {
        List<String> cCommands = Config.consoleCommands;
        if (!cCommands.isEmpty()) {
            for (String command : cCommands) {
                if (!command.equalsIgnoreCase("command 1") || !command.equalsIgnoreCase("command 2")) {
                    String comm = command.replace("/", "");
                    Sponge.getCommandManager().process(Sponge.getServer().getConsole(), comm);
                }
            }
        }
    }

    public void sendMessage(CommandSource sender, String message) {
        sender.sendMessage(fromLegacy(message));
    }

    public Text fromLegacy(String legacy) {
        return TextSerializers.FORMATTING_CODE.deserializeUnchecked(legacy);
    }

}
