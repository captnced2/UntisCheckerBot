package org.captnced.discord.messages;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class Replies {

    public static MessageEmbed makeEmbed(String title, String text, Color color) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(title);
        eb.setDescription(text);
        eb.setColor(color);
        return eb.build();
    }

    public static MessageEmbed makeEmbed(String title, Color color) {
        return makeEmbed(title, null, color);
    }

    public static MessageEmbed error() {
        return makeEmbed("Error", Color.RED);
    }

    public static MessageEmbed error(String message) {
        return makeEmbed("Error", message, Color.RED);
    }
}
