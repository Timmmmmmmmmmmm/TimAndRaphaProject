package Gui.BoardGui;

public class HistoryMove extends Move {

    public Piece.Type piece;
    public boolean capture;

    public boolean castleKingSide;
    public boolean castleQueenSide;

    public Piece.Type promotionType;

    public boolean check;
    public boolean checkmate;

    public HistoryMove(Move m) {
        super(m.fromRow, m.fromColumn, m.toRow, m.toColumn);

        this.castle = m.castle;
        this.enPassant = m.enPassant;
        this.promotion = m.promotion;
    }
}