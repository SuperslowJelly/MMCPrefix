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

    public static List<String> prefixBlacklist;

    public Config(Main main) throws IOException, ObjectMappingException {
        plugin = main;
        loader = HoconConfigurationLoader.builder().setPath(plugin.defaultConf).build();
        config = loader.load();
        configCheck();
    }

    public void configCheck() throws IOException, ObjectMappingException {

        if (!plugin.defaultConfFile.exists()) {
            plugin.defaultConfFile.createNewFile();
        }

        prefixBlacklist = checkList(config.getNode("prefix", "blacklist"), prefixBlacklistList, "Blacklist of words you cannot have in a prefix").getList(TypeToken.of(String.class));
        prefixMaxCharacterLimit = check(config.getNode("prefix", "max-character-limit"), 10, "How many characters are allowed in the prefix").getInt();
        prefixFormat = check(config.getNode("format"), "&f[%prefix%&f]", "Format of the prefix, %prefix% is replaced.").getString();

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
