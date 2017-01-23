package net.moddedminecraft.mmcprefix;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
public class Config {

    private final Main plugin;

    private static ConfigurationLoader<CommentedConfigurationNode> loader;
    public static CommentedConfigurationNode config;

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

    public static List<String> prefixBlacklist;

    public Config(Main main) throws IOException, ObjectMappingException {
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
                                                                + "{prefix} - will replace with the prefix defined from the list. /m"
                                                                + "{playername} - Will replace with the player's name").getString();
        prefixListHeader = check(config.getNode("list", "header"), "&3Click on the prefix you would like to use.", "Text to be shown at the top of the list").getString();

        if (!config.getNode("list").hasMapChildren()) {
            check(config.getNode("list", "content", "1", "prefix"), "[&6Default&f] ", "Prefix to be displayed (This is formatted differently from the default format node)");
            check(config.getNode("list", "content", "1", "permission"), "default", "Permission node for use with: mmcprefix.list.####");
        }


        loader.save(config);
    }

    private CommentedConfigurationNode check(CommentedConfigurationNode node, Object defaultValue, String comment) {
        if (node.isVirtual()) {
            node.setValue(defaultValue).setComment(comment);
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
