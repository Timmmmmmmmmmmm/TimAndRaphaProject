package Gui.util;

public class Move {

    public int fromRow, fromColumn;
    public int toRow, toColumn;

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