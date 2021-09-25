package com.github.superslowjelly.prefixmanager;

import com.github.superslowjelly.prefixmanager.commands.ACommandRegistry;
import com.github.superslowjelly.prefixmanager.commands.executors.ExecutorSetPrefix;
import com.github.superslowjelly.prefixmanager.configuration.ConfigManager;
import com.github.superslowjelly.prefixmanager.configuration.main.MainConfig;
import com.github.superslowjelly.prefixmanager.configuration.main.categories.PrefixCategory;
import com.github.superslowjelly.prefixmanager.utilities.TaskHelper;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.permission.SubjectData;

import java.io.File;

@Plugin(
    id = "prefixmanager",
    name = "prefixmanager",
    version = "1.3.0",
    description = "A simple and easy to use custom prefix manager, based on MMCPrefix, adapted by Jelly.",
    authors = {
        "leelawd",
        "SuperslowJelly"
    }
)
public class PrefixManager {

    // Static methods.
    public static PrefixManager get() { return PrefixManager.instance; }

    public static Logger getLogger() { return PrefixManager.get().logger; }

    public static ConfigManager getConfigManager() {
        return PrefixManager.get().configManager;
    }

    public static MainConfig getConfig() {
        return PrefixManager.getConfigManager().getMainConfig();
    }

    public static boolean reload() {
        try {
            TaskHelper.async(() -> {
                ACommandRegistry.COMMANDS.clear();
                PrefixManager.getConfigManager().reload();
                ACommandRegistry.registerCommands();
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Static variables.
    private static PrefixManager instance;

    // Instance variables.
    private ConfigManager configManager;

    // Injections.
    @Inject @ConfigDir(sharedRoot = true) private File configDir;

    @Inject private Logger logger;

    // Listeners.
    @Listener public void onGameConstruction(GameConstructionEvent event) { PrefixManager.instance = this; }

    @Listener public void onGamePreInit(GamePreInitializationEvent event) {
        this.logger.info("Loading...");
        TaskHelper.async(() -> {
            this.configManager = new ConfigManager(this.configDir);
            ACommandRegistry.registerCommands();
        });
        this.logger.info("Loaded!");
    }

    @Listener public void onGameReload(GameReloadEvent event) {
        this.logger.warn("Reloading...");
        if (PrefixManager.reload()) this.logger.info("Reloaded successfuly!");
        else this.logger.error("Failed to reload!");
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event, @First Player player) {
        if (!player.hasPlayedBefore() ||
            !player.hasPermission("prefixmanager.prefix." + PrefixCategory.getPrefixName(player.getSubjectData().getOptions(SubjectData.GLOBAL_CONTEXT).get("prefix")))
        ) ExecutorSetPrefix.setPrefix(player, PrefixCategory.getHighestPrefix(player));
    }
}
