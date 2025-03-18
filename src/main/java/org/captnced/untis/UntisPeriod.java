package org.captnced.untis;

import de.keule.webuntis.response.Period;

public record UntisPeriod(UntisTeacher teacher, String klass, int start, int end, int date, String room, String subject,
                          boolean canceled) {
    public UntisPeriod(Period p) {
        this(Untis.getTeacher(p), !p.getKlassen().isEmpty() ? p.getKlassen().getFirst().getName() : null, p.getStartTime(), p.getEndTime(), p.getDate(), !p.getRooms().isEmpty() ? p.getRooms().getFirst().getName() : null, !p.getSubjects().isEmpty() ? p.getSubjects().getFirst().getName() : null, p.isCancled());
    }
}
