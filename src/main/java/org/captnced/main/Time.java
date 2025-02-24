package org.captnced.main;


import java.text.*;
import java.util.*;

public class Time {

    private static final HashMap<Integer, Integer> lessons;

    static {
        lessons = new HashMap<>();
        lessons.put(1, 800);
        lessons.put(2, 845);
        lessons.put(3, 930);
        lessons.put(4, 1040);
        lessons.put(5, 1125);
        lessons.put(6, 1210);
        lessons.put(7, 1255);
        lessons.put(8, 1340);
        lessons.put(9, 1430);
        lessons.put(10, 1515);
        lessons.put(11, 1600);
        lessons.put(12, 1645);
    }

    public static List<Date> get2WeekDates() {
        List<Date> dates = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
            dates.add(cal.getTime());
            cal.add(Calendar.DATE, 1);
        }
        cal.add(Calendar.DATE, 2);
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
            dates.add(cal.getTime());
            cal.add(Calendar.DATE, 1);
        }
        return dates;
    }

    public static Date dateFromUntis(int untisDate) {
        String dateString = String.valueOf(untisDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date;
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return date;
    }

    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        return sdf.format(date);
    }

    public static int formatUntisDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return Integer.parseInt(sdf.format(date));
    }

    public static int getUntisStartFromLesson(int lesson) {
        return lessons.get(lesson);
    }

    public static int getLessonFromUntisStart(int untisStart) {
        for (HashMap.Entry<Integer, Integer> entry : lessons.entrySet()) {
            if (entry.getValue() == untisStart) {
                return entry.getKey();
            }
        }
        return -1;
    }
}
