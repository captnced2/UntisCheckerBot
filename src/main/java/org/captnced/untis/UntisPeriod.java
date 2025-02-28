package org.captnced.untis;

import de.keule.webuntis.response.Period;

public record UntisPeriod(UntisTeacher teacher, String klass, int start, int end, int date, String room, String subject,
                          boolean canceled) {
    public UntisPeriod(Period p) {
        this(Untis.getTeacher(p), p.getKlassen().getFirst().getName(), p.getStartTime(), p.getEndTime(), p.getDate(), p.getRooms().getFirst().getName(), p.getSubjects().getFirst().getName(), p.isCancled());
    }
}
