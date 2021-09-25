package com.github.superslowjelly.prefixmanager.utilities;

import io.github.nucleuspowered.nucleus.api.NucleusAPI;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

public class Utils {

    public static String getNick(Player player) {
        if (!Sponge.getPluginManager().getPlugin("nucleus").isPresent()) return player.getName();
        return TextSerializers.FORMATTING_CODE.serialize(NucleusAPI.getNicknameService().get().getNickname(player).orElse(Text.of(player.getName())));
    }
}
