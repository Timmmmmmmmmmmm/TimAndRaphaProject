package gui.dto;

public record GameRoundPlayerDto(GameDto gameDto, RoundDto roundDto, PlayerDto whitePlayer, PlayerDto blackPlayer) {

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