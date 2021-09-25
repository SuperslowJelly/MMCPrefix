package com.github.superslowjelly.prefixmanager.commands;

import com.github.superslowjelly.prefixmanager.PrefixManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.command.spec.CommandSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class ACommandRegistry {

    public static final ArrayList<ACommandRegistry> COMMANDS = new ArrayList<>();

    public static boolean alreadyContained(ACommandRegistry command) {
        if (!COMMANDS.isEmpty()) {
            for (ACommandRegistry commandRegistry : COMMANDS) {
                if (commandRegistry.getClazz().equals(command.getClazz())) return true;
            }
        }
        return false;
    }

    public static void registerCommands() {
        Set<CommandMapping> commandMappings = Sponge.getCommandManager().getOwnedBy(PrefixManager.get());
        if (!commandMappings.isEmpty()) {
            for (CommandMapping commandMapping : Sponge.getCommandManager().getOwnedBy(PrefixManager.get())) Sponge.getCommandManager().removeMapping(commandMapping);
        }
        for (ACommandRegistry command : COMMANDS) {
            if (command.getEnabled()) command.register();
        }
    }

    public abstract void register();

    public abstract boolean getEnabled();

    public abstract String getPermission();

    public abstract String getDescription();

    public abstract List<String> getAliases();

    public abstract Class<?> getClazz();

    public abstract CommandSpec getSpec();
}
