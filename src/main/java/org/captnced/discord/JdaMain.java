package org.captnced.discord;

import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.interactions.commands.*;
import net.dv8tion.jda.api.interactions.commands.build.*;
import org.captnced.configs.Config;
import org.captnced.discord.listeners.*;
import org.captnced.untis.Untis;

import java.util.*;

public class JdaMain {

    private final JDA jda;
    private final Untis untis;

    public JdaMain(Config conf, Untis unt) {
        String token = conf.getValueFromKey("token");
        if (token == null) {
            throw new RuntimeException("No token provided");
        }
        jda = JDABuilder.createDefault(token)
                .setStatus(OnlineStatus.ONLINE)
                .addEventListeners(new SlashCommandListener(unt), new CommandAutoCompleteListener(unt))
                .build();
        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        untis = unt;
        registerCommands();
    }

    private void registerCommands() {
        List<CommandData> commands = new ArrayList<>();

        OptionData dateOption = new OptionData(OptionType.INTEGER, "datum", "Datum", true, true);
        commands.add(Commands.slash("freerooms", "Gibt alle freien Räume für einen Tag und eine Stunde aus")
                .addOptions(dateOption,
                        new OptionData(OptionType.INTEGER, "stunde", "Stunde", true)
                                .addChoices(lessonChoices())));
        commands.add(Commands.slash("teachertimetable", "Gibt alle Stunden eines Lehrers für einen Tag aus")
                .addOptions(dateOption,
                        new OptionData(OptionType.STRING, "lehrer", "Lehrer", true, true)));
        jda.updateCommands().addCommands(commands).queue();
    }

    private Collection<Command.Choice> lessonChoices() {
        Collection<Command.Choice> choices = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            choices.add(new Command.Choice(i + ". Stunde", i));
        }
        return choices;
    }
}
