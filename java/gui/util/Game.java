package gui.util;

import gui.dto.GameDto;
import gui.dto.PlayerDto;
import gui.dto.RoundDto;
import gui.dto.TournamentDto;

public class Game extends SimpleGame {

    public final TournamentDto tournamentDto;
    public final RoundDto roundDto;
    public final GameDto gameDto;
    public final PlayerDto whitePlayerDto;
    public final PlayerDto blackPlayerDto;

    public Game(TournamentDto tournamentDto, RoundDto roundDto, GameDto gameDto, PlayerDto whitePlayerDto, PlayerDto blackPlayerDto) {
        super();
        this.tournamentDto = tournamentDto;
        this.roundDto = roundDto;
        this.gameDto = gameDto;
        this.whitePlayerDto = whitePlayerDto;
        this.blackPlayerDto = blackPlayerDto;
    }
}
