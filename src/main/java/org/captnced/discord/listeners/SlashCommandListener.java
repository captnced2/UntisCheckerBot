package org.captnced.discord.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.captnced.discord.messages.Replies;
import org.captnced.main.Time;
import org.captnced.untis.*;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.*;

public class SlashCommandListener extends ListenerAdapter {

    private final Untis untis;

    public SlashCommandListener(Untis unt) {
        untis = unt;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        try {
            switch (event.getName()) {
                case "freerooms" -> freerooms(event);
                case "teachertimetable" -> teachertimetable(event);
            }
        } catch (Exception e) {
            if (!event.isAcknowledged()) {
                event.replyEmbeds(Replies.error(e.getMessage())).queue();
            } else {
                event.getHook().sendMessageEmbeds(Replies.error(e.getMessage())).queue();
            }
        }
    }

    private void freerooms(SlashCommandInteractionEvent event) {
        OptionMapping date = event.getOption("datum");
        OptionMapping lesson = event.getOption("stunde");
        if (date == null || lesson == null) {
            event.replyEmbeds(Replies.error()).setEphemeral(true).queue();
            return;
        }
        event.deferReply().queue();
        untis.requestRooms(date.getAsInt());
        List<UntisRoom> rooms = untis.filterValidRooms(untis.getFreeRooms(date.getAsInt(), Time.getUntisStartFromLesson(lesson.getAsInt())));
        String title = "Freie Räume " + Time.formatDate(Time.dateFromUntis(date.getAsInt())) + " - " + lesson.getAsString() + ". Stunde";
        if (rooms.isEmpty()) {
            event.getHook().sendMessageEmbeds(Replies.makeEmbed(title, "*Keine freien Räume*", Color.yellow)).queue();
            return;
        }
        StringBuilder roomNames = new StringBuilder();
        for (UntisRoom room : rooms) {
            roomNames.append(room.name()).append("\n");
        }
        event.getHook().sendMessageEmbeds(Replies.makeEmbed(title, roomNames.toString(), Color.green)).queue();
    }

    private void teachertimetable(SlashCommandInteractionEvent event) {
        OptionMapping date = event.getOption("datum");
        OptionMapping teacher = event.getOption("lehrer");
        if (date == null || teacher == null) {
            event.replyEmbeds(Replies.error()).setEphemeral(true).queue();
            return;
        }
        event.deferReply().queue();
        untis.requestRooms(date.getAsInt());
        List<UntisPeriod> periods = untis.getTeacherTimetable(untis.findTeacher(teacher.getAsString()), date.getAsInt());
        String title = "Stunden " + untis.findTeacher(teacher.getAsString()).full() + " - " + Time.formatDate(Time.dateFromUntis(date.getAsInt()));
        if (periods.isEmpty()) {
            event.getHook().sendMessageEmbeds(Replies.makeEmbed(title, "*Keine Stunden*", Color.yellow)).queue();
            return;
        }
        periods.sort(Comparator.comparing(UntisPeriod::start));
        StringBuilder lessons = new StringBuilder();
        for (UntisPeriod period : periods) {
            lessons.append(Time.getLessonFromUntisStart(period.start())).append("\\. Stunde: ").append(period.room()).append(" - ").append(period.klass());
            if (period.canceled()) lessons.append(" (entfällt)");
            lessons.append("\n");
        }
        event.getHook().sendMessageEmbeds(Replies.makeEmbed(title, lessons.toString(), Color.green)).queue();
    }
}
