package com.github.superslowjelly.prefixmanager.utilities;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.ArrayList;
import java.util.Map;

public class Texts {

    public static Text of(String input) {
        return TextSerializers.FORMATTING_CODE.deserialize(input);
    }

    public static ArrayList<Text> of(ArrayList<String> input) {
        ArrayList<Text> output = new ArrayList<>();
        for (String string : input) {
            output.add(Texts.of(string));
        }
        return output;
    }

    public static ArrayList<Text> of(ArrayList<String> input, Map<String, String> replacements) {
        ArrayList<Text> output = new ArrayList<>();
        for (String string : input) {
            for (Map.Entry<String, String> entry : replacements.entrySet()) {
                string = string.replace(entry.getKey(), entry.getValue());
            }
            output.add(Texts.of(string));
        }
        return output;
    }

    public static String strip(String input) { return TextSerializers.FORMATTING_CODE.stripCodes(input); }
}
