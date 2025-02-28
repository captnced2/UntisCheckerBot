package org.captnced.discord.listeners;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.captnced.main.Time;
import org.captnced.untis.Untis;

import java.util.List;

public class CommandAutoCompleteListener extends ListenerAdapter {

    private final Untis untis;

    public CommandAutoCompleteListener(Untis unt) {
        untis = unt;
        untis.requestAllTeachers();
        untis.requestAllRooms();
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (event.getFocusedOption().getName().equals("datum")) {
            List<Command.Choice> choices = Time.get2WeekDates().stream()
                    .map(date -> new Command.Choice(Time.formatDate(date), Time.formatUntisDate(date)))
                    .toList();
            event.replyChoices(choices).queue();
        } else if (event.getFocusedOption().getName().equals("lehrer")) {
            List<Command.Choice> choices = untis.getAllTeachers().stream()
                    .filter(teacher -> teacher.full().toLowerCase().startsWith(event.getFocusedOption().getValue().toLowerCase()))
                    .limit(25)
                    .map(teacher -> new Command.Choice(teacher.full(), teacher.name()))
                    .toList();
            event.replyChoices(choices).queue();
        } else if (event.getFocusedOption().getName().equals("raum")) {
            List<Command.Choice> choices = untis.getAllRooms().stream()
                    .filter(room -> room.name().toLowerCase().startsWith(event.getFocusedOption().getValue().toLowerCase()))
                    .limit(25)
                    .map(room -> new Command.Choice(room.name(), room.name()))
                    .toList();
            event.replyChoices(choices).queue();
        }
    }
}
