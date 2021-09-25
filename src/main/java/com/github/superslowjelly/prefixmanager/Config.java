package com.github.superslowjelly.prefixmanager;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
public class Config {

    private final PrefixManager plugin;

    private static ConfigurationLoader<CommentedConfigurationNode> loader;
    public static CommentedConfigurationNode config;

    public static String prefix = "&f[&6MMCPrefix&f] ";

    private String[] prefixBlacklistList = {
            "owner",
            "admin",
            "mod",
            "staff",
            "trialmod"
    };

    public static int prefixMaxCharacterLimit;
    public static String prefixFormat;
    public static String prefixListTitle;
    public static String prefixListHover;
    public static String prefixListHeader;
    public static int prefixListCooldown;

    public static List<String> consoleCommands;

    public static List<String> prefixBlacklist;

    public static String messageAddPrefixSucess;
    public static String messageDelPrefixOtherSucess;
    public static String messageDelPrefixSelfSucess;
    public static String messageSetPrefixOtherSucess;
    public static String messageSetPrefixSelfSucess;
    public static String messageSetPrefixSelfReset;

    public Config(PrefixManager main) throws IOException, ObjectMappingException {
        plugin = main;
        loader = HoconConfigurationLoader.builder().setPath(plugin.defaultConf).build();
        config = loader.load();
        configCheck();
    }

    public static CommentedConfigurationNode getConfig() {
        return config;
    }

    public void configCheck() throws IOException, ObjectMappingException {

        if (!plugin.defaultConfFile.exists()) {
            plugin.defaultConfFile.createNewFile();
        }

        prefixBlacklist = checkList(config.getNode("prefix", "blacklist"), prefixBlacklistList, "Blacklist of words you cannot have in a prefix").getList(TypeToken.of(String.class));
        prefixMaxCharacterLimit = check(config.getNode("prefix", "max-character-limit"), 10, "How many characters are allowed in the prefix").getInt();
        prefixFormat = check(config.getNode("format"), "&f[%prefix%&f]", "Format of the prefix, %prefix% is replaced.").getString();

        prefixListTitle = check(config.getNode("list", "title"), "Prefix List", "Title to be shown during /prefixlist").getString();
        prefixListHover = check(config.getNode("list", "hover"), "Set your current prefix to: {prefix}{playername}", "Text to be shown while hovering over each prefix. \n"
                                                                + "{prefix} - will replace with the prefix defined from the list. \n"
                                                                + "{playername} - Will replace with the player's name").getString();
        prefixListHeader = check(config.getNode("list", "header"), "&3Click on the prefix you would like to use.", "Text to be shown at the top of the list").getString();
        prefixListCooldown = check(config.getNode("list", "cooldown"), 5, "How long should a person have to wait before selecting another prefix? (in minutes) (Set to 0 to disable)").getInt();

        if (!config.getNode("list", "content").hasMapChildren()) {
            check(config.getNode("list", "content", "1", "prefix"), "[&6Default&f] ", "Prefix to be displayed (This is formatted differently from the default format node)");
            check(config.getNode("list", "content", "1", "permission"), "default", "Permission node for use with: mmcprefix.list.#### \n"
                                                                                + "This can be replaced with \"\" to allow use without a permission.");
        }

        if (config.getNode("prefix", "on-change", "console-commands").hasListChildren()) {
            consoleCommands = check(config.getNode("prefix", "on-change", "console-commands"), Collections.emptyList(), "Commands to be run after the player changes a prefix").getList(TypeToken.of(String.class));
        } else {
            consoleCommands = config.getNode("prefix", "on-change", "console-commands").setValue(Collections.emptyList()).setComment("Commands to be run after the player changes a prefix").getList(TypeToken.of(String.class));
        }

        messageAddPrefixSucess = check(config.getNode("messages", "addPrefix", "success"), "&6{playername} &3now has the permission: &6{permission}").getString();
        messageDelPrefixOtherSucess = check(config.getNode("messages", "delPrefix", "other", "success"), "&3Prefix Deleted for &6{playername} &3!").getString();
        messageDelPrefixSelfSucess = check(config.getNode("messages", "delPrefix", "self", "success"), "&3Prefix Deleted!").getString();
        messageSetPrefixOtherSucess = check(config.getNode("messages", "setPrefix", "other", "success"), "&3Prefix Set for &6{playername}&3: {prefix}").getString();
        messageSetPrefixSelfSucess = check(config.getNode("messages", "setPrefix", "self", "success"), "&3Prefix Set to: &f{prefix}").getString();
        messageSetPrefixSelfReset = check(config.getNode("messages", "setPrefix", "self", "reset"), "&3Prefix has been reset!").getString();

        loader.save(config);
    }

    private CommentedConfigurationNode check(CommentedConfigurationNode node, Object defaultValue, String comment) {
        if (node.isVirtual()) {
            node.setValue(defaultValue).setComment(comment);
        }
        return node;
    }

    private CommentedConfigurationNode check(CommentedConfigurationNode node, Object defaultValue) {
        if (node.isVirtual()) {
            node.setValue(defaultValue);
        }
        return node;
    }

    private CommentedConfigurationNode checkList(CommentedConfigurationNode node, String[] defaultValue, String comment) {
        if (node.isVirtual()) {
            node.setValue(Arrays.asList(defaultValue)).setComment(comment);
        }
        return node;
    }
}
