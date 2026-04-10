package gui.dto;

import gui.DatabaseConnection;
import gui.util.DateUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public record TournamentDto(int id, String name, LocalDate date, String city, int base_consider_time, int move_consider_time, TournamentStatus status) {

    public enum TournamentStatus {
        PLANNED, ACTIVE, COMPLETED
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
                        DateUtil.parseLocalDate(tournament.get("date")),
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
}
