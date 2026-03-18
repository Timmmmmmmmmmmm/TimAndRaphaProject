package Gui.BoardGui;

public class Piece {

    public enum Type {
        PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING
    }

    Type type;
    boolean white;
    boolean moved = false;

    public Piece(Type type, boolean white) {
        this.type = type;
        this.white = white;
    }

    public String getSymbol() {
        Character color = this.white ? 'w' : 'b';
        String piece = switch (type) {
            case KING -> "K";
            case QUEEN -> "Q";
            case ROOK -> "R";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            case PAWN -> "P";
        };
        return color + piece;
    }
}
