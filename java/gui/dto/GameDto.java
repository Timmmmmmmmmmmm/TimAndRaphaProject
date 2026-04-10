package gui.dto;

import gui.DatabaseConnection;
import gui.util.DateUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameDto {
    public final int id;
    public final Integer result;
    public final LocalDateTime start;
    public int board_number;
    public final int round_id;
    public final int player_white;
    public final int player_black;
    public final int tournament_id;

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
                        DateUtil.parseLocalDateTime(game.get("start")),
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
}
