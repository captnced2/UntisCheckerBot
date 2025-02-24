package org.captnced.untis;

public record UntisPeriod(UntisTeacher teacher, String klass, int start, int end, int date, String room,
                          boolean canceled) {
}
