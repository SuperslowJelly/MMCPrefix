package com.github.superslowjelly.prefixmanager.configuration.main.categories;

import com.github.superslowjelly.prefixmanager.commands.executors.*;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class CommandsCategory {

    @Setting(value = "prefixmanager", comment = "Main PrefixManager command.")
    public final ExecutorPrefixManager PREFIXMANAGER = new ExecutorPrefixManager();

    @Setting(value = "reload", comment = "Reloads the plugin.")
    public final ExecutorReload RELOAD = new ExecutorReload();

    @Setting(value = "addprefix", comment = "Gives the given player the permission to use the given prefix in /prefixlist.")
    public final ExecutorAddPrefix ADDPREFIX = new ExecutorAddPrefix();

    @Setting(value = "setprefix", comment = "Sets the given player's prefix to the given prefix.")
    public final ExecutorSetPrefix SETPREFIX = new ExecutorSetPrefix();

    @Setting(value = "prefixlist", comment = "Lists all available prefixes.")
    public final ExecutorPrefixList PREFIXLIST = new ExecutorPrefixList();
}
