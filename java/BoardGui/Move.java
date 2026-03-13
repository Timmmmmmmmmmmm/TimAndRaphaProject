package BoardGui;

public class Move {

    int fromRow, fromColumn;
    int toRow, toColumn;

    boolean castle = false;
    boolean enPassant = false;
    boolean promotion = false;

    Piece.Type promotionType;

    public Move(int fromRow, int fromColumn, int toRow, int toColumn){

        this.fromRow = fromRow;
        this.fromColumn = fromColumn;
        this.toRow = toRow;
        this.toColumn = toColumn;
    }
}