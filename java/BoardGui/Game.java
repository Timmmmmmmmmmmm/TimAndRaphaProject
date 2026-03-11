package BoardGui;

public class Game {
    public Piece[][] board = new Piece[8][8];

    public boolean whiteTurn = true;
    public String result = "*";

    public Game() {
        setup();
    }

    private void setup(){

        for(int i = 0;i < 8;i++){

            board[1][i] = new Piece(Piece.Type.PAWN,false);
            board[6][i] = new Piece(Piece.Type.PAWN,true);
        }

        board[0][0] = new Piece(Piece.Type.ROOK,false);
        board[0][7] = new Piece(Piece.Type.ROOK,false);

        board[7][0] = new Piece(Piece.Type.ROOK,true);
        board[7][7] = new Piece(Piece.Type.ROOK,true);

        board[0][1] = new Piece(Piece.Type.KNIGHT,false);
        board[0][6] = new Piece(Piece.Type.KNIGHT,false);

        board[7][1] = new Piece(Piece.Type.KNIGHT,true);
        board[7][6] = new Piece(Piece.Type.KNIGHT,true);

        board[0][2] = new Piece(Piece.Type.BISHOP,false);
        board[0][5] = new Piece(Piece.Type.BISHOP,false);

        board[7][2] = new Piece(Piece.Type.BISHOP,true);
        board[7][5] = new Piece(Piece.Type.BISHOP,true);

        board[0][3] = new Piece(Piece.Type.QUEEN,false);
        board[7][3] = new Piece(Piece.Type.QUEEN,true);

        board[0][4] = new Piece(Piece.Type.KING,false);
        board[7][4] = new Piece(Piece.Type.KING,true);
    }

}
