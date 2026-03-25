package Gui.Dto;

import Gui.DatabaseConnection;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameDto {
    public int id;
    public Integer result;
    public LocalDateTime start;
    public int board_number;
    public int round_id;
    public int player_white;
    public int player_black;
    public int tournament_id;

    public GameDto(int id, Integer result, LocalDateTime start, int board_number, int round_id, int player_white, int player_black, int tournament_id) {
        this.id = id;
        this.result = result;
        this.start = start;
        this.board_number = board_number;
        this.round_id = round_id;
        this.player_white = player_white;
        this.player_black = player_black;
        this.tournament_id = tournament_id;
    }

    public static List<GameDto> getAsList(String sql) {
        List<HashMap<String, String>> gamesHashMap = DatabaseConnection.executeSql(sql);
        if (gamesHashMap == null || gamesHashMap.isEmpty()) {
            return null;
        }
        List<GameDto> gameList = new ArrayList<>();

        for (HashMap<String, String> game : gamesHashMap) {
            try {
                gameList.add(new GameDto(
                        Integer.parseInt(game.get("id")),
                        game.get("result") == null ? null :Integer.parseInt(game.get("result")),
                        parseLocalDateTime(game.get("start")),
                        Integer.parseInt(game.get("board_number")),
                        Integer.parseInt(game.get("round_id")),
                        Integer.parseInt(game.get("player_white")),
                        Integer.parseInt(game.get("player_black")),
                        Integer.parseInt(game.get("tournament_id"))
                ));
            } catch (Exception ignored) {
            }
        }

        return gameList;
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
