package org.captnced.untis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.keule.webuntis.WebUntis;
import de.keule.webuntis.response.*;
import org.json.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Untis extends WebUntis {

    private final ObjectMapper mapper;
    private final File roomsFile;
    private final String[] roomsBlacklist = new String[]{"A1U15", "AB1", "AB2", "A013"};
    private List<UntisTeacher> allTeachers;
    private List<UntisRoom> allRooms;

    public Untis() {
        super("Gym_MT", "nessa.webuntis.com");
        mapper = new ObjectMapper();
        roomsFile = new File("Rooms.txt");
        allTeachers = new ArrayList<>();
    }

    public static UntisTeacher getTeacher(Period p) {
        JSONArray arr = p.getJSON().getJSONArray("te");
        String name;
        String longName;
        if (arr == null || arr.isEmpty()) return null;
        name = ((JSONObject) arr.get(0)).get("name").toString();
        longName = ((JSONObject) arr.get(0)).get("longname").toString();
        if (name.isBlank() || longName.isBlank()) return null;
        return new UntisTeacher(name, longName);
    }

    public void requestRooms(int date) {
        try {
            this.logout();
            this.login();
            List<UntisRoom> untisRooms = new ArrayList<>();
            List<Klasse> klas = this.getKlassen().getKlassen();
            for (Klasse k : klas) {
                for (Period p : (date == -1) ? this.getTimetableForWeek(k).getPeriods() : this.getTimetableFor(date, 1, k.getId()).getPeriods()) {
                    boolean exists = false;
                    for (UntisRoom r : untisRooms) {
                        if (!p.getRooms().isEmpty() && r.name().equals(p.getRooms().getFirst().getName())) {
                            if (getTeacher(p) != null) {
                                boolean newLesson = true;
                                for (UntisPeriod per : r.periods()) {
                                    if (p.getStartTime() == per.start()) newLesson = false;
                                }
                                if (newLesson) {
                                    r.periods().add(new UntisPeriod(p));
                                }
                            }
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        if (!p.getRooms().isEmpty()) {
                            UntisRoom nr = new UntisRoom(p.getRooms().getFirst().getName(), new ArrayList<>());
                            if (getTeacher(p) != null)
                                nr.periods().add(new UntisPeriod(p));
                            untisRooms.add(nr);
                        }
                    }
                }
            }
            this.logout();
            untisRooms.sort(Comparator.comparing(UntisRoom::name));
            mapper.writeValue(roomsFile, untisRooms);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<UntisRoom> filterValidRooms(List<UntisRoom> untisRooms) {
        return untisRooms.stream()
                .filter(untisRoom -> untisRoom.name().startsWith("A") || untisRoom.name().startsWith("B") || untisRoom.name().startsWith("C") || untisRoom.name().equals("MT1") || untisRoom.name().equals("MT2") || untisRoom.name().equals("MT3"))
                .filter(untisRoom -> Arrays.stream(roomsBlacklist).noneMatch(untisRoom.name()::equals))
                .collect(Collectors.toList());
    }

    public List<UntisRoom> getFreeRooms(int date, int start) {
        List<UntisRoom> all = readRooms();
        List<UntisRoom> freeRooms = new ArrayList<>();
        for (UntisRoom r : all) {
            boolean free = true;
            for (UntisPeriod p : r.periods()) {
                if (p.date() == date && p.start() == start) {
                    free = false;
                    break;
                }
            }
            if (free) {
                freeRooms.add(r);
            }
        }
        return freeRooms;
    }

    public void requestAllRooms() {
        List<UntisRoom> untisRooms = new ArrayList<>();
        try {
            this.logout();
            this.login();
            Rooms rooms = this.getRooms();
            for (Room r : rooms.getRooms()) {
                untisRooms.add(new UntisRoom(r.getName(), new ArrayList<>()));
            }
            untisRooms.sort(Comparator.comparing(UntisRoom::name));
            allRooms = filterValidRooms(untisRooms);
            this.logout();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<UntisRoom> getAllRooms() {
        return allRooms;
    }

    public void requestAllTeachers() {
        requestRooms(-1);
        List<UntisRoom> all = readRooms();
        List<UntisTeacher> teachers = new ArrayList<>();
        all.forEach(room -> room.periods().forEach(period -> {
            boolean exists = false;
            for (UntisTeacher teacher : teachers) {
                if (teacher.name().equals(period.teacher().name())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                teachers.add(period.teacher());
            }
        }));
        allTeachers = teachers;
    }

    public UntisTeacher findTeacher(String name) {
        for (UntisTeacher t : allTeachers) {
            if (t.name().equals(name)) {
                return t;
            }
        }
        return null;
    }

    public List<UntisTeacher> getAllTeachers() {
        return allTeachers;
    }

    public List<UntisPeriod> getTeacherTimetable(UntisTeacher teacher, int date) {
        List<UntisRoom> all = readRooms();
        List<UntisPeriod> teacherTimetable = new ArrayList<>();
        for (UntisRoom r : all) {
            for (UntisPeriod p : r.periods()) {
                if (p.date() == date && p.teacher().name().equals(teacher.name())) {
                    teacherTimetable.add(p);
                }
            }
        }
        teacherTimetable.sort(Comparator.comparing(UntisPeriod::start));
        return teacherTimetable;
    }

    public List<UntisPeriod> getRoomTimetable(String roomName) {
        List<UntisRoom> all = readRooms();
        List<UntisPeriod> roomTimetable = new ArrayList<>();
        for (UntisRoom r : all) {
            if (r.name().equals(roomName)) {
                roomTimetable = r.periods();
            }
        }
        return roomTimetable;
    }

    public List<UntisRoom> readRooms() {
        try {
            return mapper.readValue(roomsFile, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
