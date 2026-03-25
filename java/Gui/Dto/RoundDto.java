package Gui.Dto;

import Gui.DatabaseConnection;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RoundDto {
    public int id;
    public int round_number;
    public RoundStatus status;
    public LocalDateTime begin;

    public RoundDto(int id, int round_number, RoundStatus status, LocalDateTime begin) {
        this.id = id;
        this.round_number = round_number;
        this.status = status;
        this.begin = begin;
    }

    public static List<RoundDto> getAsList(String sql) {
        List<HashMap<String, String>> roundsHashMap = DatabaseConnection.executeSql(sql);
        if (roundsHashMap == null || roundsHashMap.isEmpty()) {
            return null;
        }
        List<RoundDto> roundList = new ArrayList<>();

        for (HashMap<String, String> round : roundsHashMap) {
            try {
                roundList.add(new RoundDto(
                        Integer.parseInt(round.get("id")),
                        Integer.parseInt(round.get("round_number")),
                        RoundStatus.valueOf(round.get("status")),
                        parseLocalDateTime(round.get("begin"))
                ));
            } catch (Exception ignored) {
            }
        }

        return roundList;
    }

    public enum RoundStatus {
        PLANNED(0), RUNNING(1), COMPLETED(2);
        final int key;
        RoundStatus(int key) {
            this.key = key;
        }
    }

    public static LocalDate parseLocalDate(String s) {
        for (String f : new String[]{"yyyy-MM-dd","dd.MM.yyyy","yyyy/MM/dd"})
            try { return LocalDate.parse(s, DateTimeFormatter.ofPattern(f)); } catch (DateTimeParseException ignored) {}
        throw new DateTimeParseException("No format", s, 0);
    }

    public static LocalDateTime parseLocalDateTime(String s) {
        for (String f : new String[]{"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd'T'HH:mm:ss","yyyy/MM/dd HH:mm:ss","dd.MM.yyyy HH:mm:ss"})
            try { return LocalDateTime.parse(s, DateTimeFormatter.ofPattern(f)); } catch (DateTimeParseException ignored) {}
        throw new DateTimeParseException("No format", s, 0);
    }
}
