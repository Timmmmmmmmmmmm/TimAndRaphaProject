package Gui.BoardGui;

import Gui.Dto.Player;
import Gui.Dto.Round;
import Gui.Dto.Tournament;

import java.util.ArrayList;
import java.util.List;

public class Game {
    public Piece[][] board = new Piece[8][8];

    public List<HistoryMove> history = new ArrayList<>();
    public boolean whiteTurn = true;
    public String result = "*";

    Tournament tournament;
    Round round;
    Player whitePlayer;
    Player blackPlayer;

    public Game(Tournament tournament, Round round, Player whitePlayer, Player blackPlayer) {
        setup();
        this.tournament = tournament;
        this.round = round;
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
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

    public Game copy(){
        Game game = new Game(null, null, null, null);

        game.whiteTurn = whiteTurn;
        game.result = result;

        game.board = new Piece[8][8];

        for(int row = 0;row < 8;row++)
            for(int column = 0;column < 8;column++){

                Piece piece = board[row][column];

                if(piece != null){

                    Piece newPiece = new Piece(piece.type,piece.white);
                    newPiece.moved = piece.moved;

                    game.board[row][column] = newPiece;
                }
            }

        return game;
    }

    public ChessResult makeMove(Move move, boolean copy) {
        Piece piece = board[move.fromRow][move.fromColumn];

        HistoryMove historyMove = new HistoryMove(move);
        historyMove.piece = piece.type;
        historyMove.capture = board[move.toRow][move.toColumn] != null || move.enPassant;
        historyMove.castleKingSide = move.castle && move.toColumn == 6;
        historyMove.castleQueenSide = move.castle && move.toColumn == 2;

        board[move.toRow][move.toColumn] = piece;
        board[move.fromRow][move.fromColumn] = null;

        if (!copy) {
            if(piece.type == Piece.Type.PAWN && Math.abs(move.fromRow - move.toRow) == 2) {
                MoveGenerator.enPassantColumn = move.toColumn;
            } else {
                MoveGenerator.enPassantColumn = -1;
            }
        }
        if (move.enPassant) {
            board[move.fromRow][move.toColumn] = null;
        }

        if(move.castle){
            if(move.toColumn == 6){
                board[move.toRow][5] = board[move.toRow][7];
                board[move.toRow][7] = null;
            }else{

                board[move.toRow][3] = board[move.toRow][0];
                board[move.toRow][0] = null;
            }
        }

        if(move.promotion){
            Piece.Type type = PromotionDialog.choosePromotion(piece.white);
            board[move.toRow][move.toColumn] = new Piece(type,piece.white);

            historyMove.promotionType = type;
        }

        piece.moved = true;
        whiteTurn = !whiteTurn;

        if (!copy) {
            List<Move> movesOther = new ArrayList<>();
            for (int row = 0; row < 8; row++) {
                for (int column = 0; column < 8; column++) {
                    if (board[row][column] != null && board[row][column].white == whiteTurn) {
                        movesOther.addAll(MoveGenerator.generateLegal(this, row, column));
                    }
                }
            }

            if (movesOther.isEmpty()) {
                if (MoveGenerator.inCheck(this, whiteTurn)) {
                    historyMove.checkmate = true;
                    history.add(historyMove);
                    return ChessResult.CHECKMATE;
                } else {
                    history.add(historyMove);
                    return ChessResult.STALEMATE;
                }
            } else {
                if (MoveGenerator.inCheck(this, whiteTurn)) {
                    historyMove.check = true;
                    history.add(historyMove);
                    return ChessResult.CHECK;
                } else {
                    history.add(historyMove);
                    return ChessResult.NONE;
                }
            }
        }
        return null;
    }

    public void resign(boolean whiteResigns){
        if(whiteResigns) {
            result = "0-1";
        } else {
            result = "1-0";
        }
    }

    public void draw(){
        result = "1/2-1/2";
    }

}
