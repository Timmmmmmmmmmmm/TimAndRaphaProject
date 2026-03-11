package BoardGui;

public class Piece {

    public enum Type{
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
        return switch (type) {
            case KING -> "king";
            case QUEEN -> "queen";
            case ROOK -> "rook";
            case BISHOP -> "bishop";
            case KNIGHT -> "knight";
            case PAWN -> "pawn";
        };
    }
}
