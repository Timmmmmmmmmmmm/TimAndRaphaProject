package gui.dto;

import gui.DatabaseConnection;
import gui.util.DateUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public record RoundDto(int id, int round_number, RoundStatus status, LocalDateTime begin) {

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
                        DateUtil.parseLocalDateTime(round.get("begin"))
                ));
            } catch (Exception ignored) {
            }
        }

        return roundList;
    }

    public enum RoundStatus {
        PLANNED, RUNNING, COMPLETED
    }

    @Override
    public String toString() {
        return "Round " + round_number;
    }
}
