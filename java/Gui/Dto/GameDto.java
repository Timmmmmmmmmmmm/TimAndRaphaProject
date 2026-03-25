package Gui.Dto;

import java.time.LocalDateTime;

public class GameDto {
    public int id;
    public int result;
    public LocalDateTime start;
    public int board_number;
    public int round_id;
    public int player_white;
    public int player_black;
    public int tournament_id;

    public GameDto(int id, int result, LocalDateTime start, int board_number, int round_id, int player_white, int player_black, int tournament_id) {
        this.id = id;
        this.result = result;
        this.start = start;
        this.board_number = board_number;
        this.round_id = round_id;
        this.player_white = player_white;
        this.player_black = player_black;
        this.tournament_id = tournament_id;
    }
}
