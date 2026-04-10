package gui.util;

public class Move {

    public final int fromRow;
    public final int fromColumn;
    public final int toRow;
    public final int toColumn;

    public boolean castle = false;
    public boolean enPassant = false;
    public boolean promotion = false;

    public Move(int fromRow, int fromColumn, int toRow, int toColumn){

        this.fromRow = fromRow;
        this.fromColumn = fromColumn;
        this.toRow = toRow;
        this.toColumn = toColumn;
    }
}