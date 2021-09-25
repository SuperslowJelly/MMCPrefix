package com.github.superslowjelly.prefixmanager.configuration.main.categories;

import com.github.superslowjelly.prefixmanager.PrefixManager;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.entity.living.player.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@ConfigSerializable
public class PrefixCategory {

    public PrefixCategory() {
        this.rawPrefixes.put("0/default", "&8&l[&7&lDefault&8&l]&r");
        this.rawPrefixes.put("1/nano", "&8&l[Nano]&r");
        this.rawPrefixes.put("2/cobalt", "&8&l[&3&lCobalt&8&l]&r");
        this.rawPrefixes.put("3/manyullyn", "&8&l[&d&lManyullyn&8&l]&r");
        this.rawPrefixes.put("4/darkmatter", "&8&l[&5&lDark&7&l-&5&lMatter&8&l]&r");
        this.rawPrefixes.put("5/redmatter", "&8&l[&c&lRed&7&l-&c&lMatter&8&l]&r");
        this.rawPrefixes.put("6/wyvern", "&8&l[&9&lWyvern&8&l]&r");
        this.rawPrefixes.put("7/draconic", "&8&l[&6&lDraconic&8&l]&r");
        this.rawPrefixes.put("8/quantum", "&8&l[&f&lQuantum&8&l]&r");
        this.rawPrefixes.put("9/beta", "&8&l[&c&lBeta&7&l-&f&lTester&8&l]&r");
        this.rawPrefixes.put("10/helper", "&8&l[&a&lHelper&8&l]&r");
        this.rawPrefixes.put("11/trialmod", "&8&l[&b&lTrial&7&l-&3&lMod&8&l]&r");
        this.rawPrefixes.put("12/moderator", "&8&l[&3&lModerator&8&l]&r");
        this.rawPrefixes.put("13/srmod", "&8&l[&f&lSr&7&l-&3&lMod&8&l]&r");
        this.rawPrefixes.put("14/admin", "&8&l[&4&lAdmin&8&l]&r");
        this.rawPrefixes.put("15/sradmin", "&8&l[&f&lSr&7&l-&4&lAdmin&8&l]&r");
        this.rawPrefixes.put("16/staffmanager", "&8&l[&f&lStaff&7&l-&4&lManager&8&l]&r");
        this.rawPrefixes.put("17/developer", "&8&l[&d&lDeveloper&8&l]&r");
        this.rawPrefixes.put("18/manager", "&8&l[&6&lManager&8&l]&r");
        this.rawPrefixes.put("19/owner", "&8&l[&b&lOwner&8&l]&r");
    }

    public static String getHighestPrefix(Player player) {
        String highest = "default";
        for (Map.Entry<String, String> entry : PrefixManager.getConfig().PREFIX.getPrefixes().entrySet()) {
            if (player.hasPermission("prefixmanager.prefix." + entry.getKey())) highest = entry.getKey();
        }
        return highest;
    }

    public static String getPrefixName(String prefix) {
        String name = "default";
        for (Map.Entry<String, String> entry : PrefixManager.getConfig().PREFIX.getPrefixes().entrySet()) {
            if (entry.getValue().equals(prefix)) {
                name = entry.getKey();
                break;
            }
        }
        return name;
    }

    @Setting(value = "prefixes", comment = "Map of prefix name to prefix value. To give permission for a prefix, use the format: \"prefixmanager.prefix.<prefix name>\".")
    public final LinkedHashMap<String, String> rawPrefixes = new LinkedHashMap<>();

    private final LinkedHashMap<String, String> PREFIXES = new LinkedHashMap<>();

    private void reloadPrefixes() {
        HashMap<String, String> source;

        if (PrefixManager.getConfigManager() != null && PrefixManager.getConfig() != null) source = PrefixManager.getConfig().PREFIX.rawPrefixes;
        else source = this.rawPrefixes;

        String[] orderedPrefixes = new String[source.size()];
        for (Map.Entry<String, String> entry : source.entrySet()) {
            String[] entryKey = entry.getKey().split("/");
            int priority = Integer.parseInt(entryKey[0]);
            orderedPrefixes[priority] = entry.getKey();
        }
        for (String orderedPrefix : orderedPrefixes) this.PREFIXES.put(orderedPrefix.split("/")[1], source.get(orderedPrefix));
    }

    public HashMap<String, String> getPrefixes() {
        if (this.PREFIXES.isEmpty()) this.reloadPrefixes();
        return this.PREFIXES;
    }
}
