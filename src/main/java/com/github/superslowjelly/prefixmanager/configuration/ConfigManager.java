package com.github.superslowjelly.prefixmanager.configuration;

import com.github.superslowjelly.prefixmanager.configuration.main.MainConfig;
import io.github.eufranio.config.Config;

import java.io.File;

public class ConfigManager {

    // Constructor.
    public ConfigManager(File configDir) {
        this.initMainConfigManager(configDir);
        this.reload();
    }

    // Instance variables.
    public Config<MainConfig> mainConfigManager;

    // Public methods.
    private void initMainConfigManager(File configDir) { this.mainConfigManager = new Config<>(MainConfig.class, "PrefixManager.conf", configDir); }

    public Config<MainConfig> getMainConfigManager() { return this.mainConfigManager; }

    public MainConfig getMainConfig() { return this.getMainConfigManager().get(); }

    public void reloadMainConfig() {
        this.getMainConfigManager().reload();
    }

    public void reload() {
        this.reloadMainConfig();
    }
}
