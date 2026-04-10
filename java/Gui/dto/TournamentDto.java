package Gui.dto;

import Gui.DatabaseConnection;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TournamentDto {
    public int id;
    public String name;
    public LocalDate date;
    public String city;
    public int base_consider_time;
    public int move_consider_time;
    public TournamentStatus status;

    public TournamentDto(int id, String name, LocalDate date, String city, int base_consider_time, int move_consider_time, TournamentStatus status) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.city = city;
        this.base_consider_time = base_consider_time;
        this.move_consider_time = move_consider_time;
        this.status = status;
    }

    public enum TournamentStatus {
        PLANNED(0), ACTIVE(1), COMPLETED(2);

        final int key;

        TournamentStatus(int key) {
            this.key = key;
        }

        public int getKey() {
            return key;
        }
    }

    public static List<TournamentDto> getAsList(String sql) {
        List<HashMap<String, String>> tournamentsHashMap = DatabaseConnection.executeSql(sql);
        if (tournamentsHashMap == null || tournamentsHashMap.isEmpty()) {
            return null;
        }
        List<TournamentDto> tournamentList = new ArrayList<>();

        for (HashMap<String, String> tournament : tournamentsHashMap) {
            try {
                tournamentList.add(new TournamentDto(
                        Integer.parseInt(tournament.get("id")),
                        tournament.get("name"),
                        parseLocalDate(tournament.get("date")),
                        tournament.get("city"),
                        Integer.parseInt(tournament.get("base_consider_time")),
                        Integer.parseInt(tournament.get("move_consider_time")),
                        TournamentStatus.valueOf(tournament.get("status"))
                ));
            } catch (Exception ignored) {
            }
        }

        return tournamentList;
    }

    @Override
    public String toString() {
        return name;
    }

    public static LocalDate parseLocalDate(String s) {
        for (String f : new String[]{"yyyy-MM-dd","dd.MM.yyyy","yyyy/MM/dd"})
            try {
                return LocalDate.parse(s, DateTimeFormatter.ofPattern(f));
            } catch (Exception ignored) {}
        return null;
    }

    public static LocalDateTime parseLocalDateTime(String s) {
        for (String f : new String[]{"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd'T'HH:mm:ss","yyyy/MM/dd HH:mm:ss","dd.MM.yyyy HH:mm:ss"})
            try {
                return LocalDateTime.parse(s, DateTimeFormatter.ofPattern(f));
            } catch (Exception ignored) {}
        return null;
    }
}
