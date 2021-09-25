package com.github.superslowjelly.prefixmanager.configuration.main;

import com.github.superslowjelly.prefixmanager.configuration.main.categories.CommandsCategory;
import com.github.superslowjelly.prefixmanager.configuration.main.categories.PrefixCategory;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class MainConfig {

    @Setting(value = "commands", comment = "Settings related to commands.")
    public final CommandsCategory COMMANDS = new CommandsCategory();

    @Setting(value = "prefixes", comment = "Settings related to prefixes.")
    public final PrefixCategory PREFIX = new PrefixCategory();
}
