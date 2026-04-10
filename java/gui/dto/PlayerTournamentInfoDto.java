package gui.dto;

import gui.DatabaseConnection;
import gui.util.DateUtil;
import gui.util.FideTitle;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerTournamentInfoDto {
    public final PlayerDto player;
    public final double score;

    public PlayerTournamentInfoDto(int id, String firstname, String lastname, int fide_rating, FideTitle fide_title, char gender, LocalDate birthdate, double score) {
        player = new PlayerDto(id, firstname, lastname, fide_rating, fide_title, gender, birthdate);
        this.score = score;
    }

    public static List<PlayerTournamentInfoDto> getAsList(String sql) {
        List<HashMap<String, String>> playersHashMap = DatabaseConnection.executeSql(sql);
        if (playersHashMap == null || playersHashMap.isEmpty()) {
            return null;
        }
        List<PlayerTournamentInfoDto> playerList = new ArrayList<>();

        for (HashMap<String, String> player : playersHashMap) {
            try {
                playerList.add(new PlayerTournamentInfoDto(
                        Integer.parseInt(player.get("player_id")),
                        player.get("firstname"),
                        player.get("lastname"),
                        Integer.parseInt(player.get("fide_rating")),
                        fromKeyOrName(player.get("fide_title")),
                        player.get("gender").charAt(0),
                        DateUtil.parseLocalDate(player.get("birthdate")),
                        Double.parseDouble(player.get("score"))
                ));
            } catch (Exception ignored) {
            }
        }

        return playerList;
    }

    public static FideTitle fromKeyOrName(String value) {
        for (FideTitle fideTitle : FideTitle.values()) {
            if (value.toUpperCase().equals(fideTitle.getKey()) || value.toUpperCase().equals(fideTitle.name())) {
                return fideTitle;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return player.lastname + ", " + player.firstname;
    }
}
