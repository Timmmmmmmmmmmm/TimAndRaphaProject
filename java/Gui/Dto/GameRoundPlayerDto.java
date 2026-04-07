package Gui.Dto;

public class GameRoundPlayerDto {
    public GameDto gameDto;
    public RoundDto roundDto;
    public PlayerDto whitePlayer;
    public PlayerDto blackPlayer;

    public GameRoundPlayerDto(GameDto gameDto, RoundDto roundDto, PlayerDto whitePlayer, PlayerDto blackPlayer) {
        this.gameDto = gameDto;
        this.roundDto = roundDto;
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
    }

    public String toString() {
        if (gameDto.result == null) {
            return whitePlayer + " vs " + blackPlayer;
        }

        return switch (gameDto.result) {
            case 0 -> whitePlayer + " vs " + blackPlayer + " (½ - ½)";
            case 1 -> whitePlayer + " vs " + blackPlayer + " (1 - 0)";
            case -1 -> whitePlayer + " vs " + blackPlayer + " (0 - 1)";
            default -> whitePlayer + " vs " + blackPlayer;
        };
    }
}