package Gui.BoardGui;

import Gui.Dto.GameDto;
import Gui.Dto.PlayerDto;
import Gui.Dto.RoundDto;
import Gui.Dto.TournamentDto;

import java.util.ArrayList;
import java.util.List;

public class Game {
    public Piece[][] board = new Piece[8][8];

    public List<String> history = new ArrayList<>();
    public boolean whiteTurn = true;
    public String result = "*";

    TournamentDto tournamentDto;
    RoundDto roundDto;
    GameDto gameDto;
    PlayerDto whitePlayerDto;
    PlayerDto blackPlayerDto;

    public Game(TournamentDto tournamentDto, RoundDto roundDto, GameDto gameDto, PlayerDto whitePlayerDto, PlayerDto blackPlayerDto) {
        setup();
        this.tournamentDto = tournamentDto;
        this.roundDto = roundDto;
        this.gameDto = gameDto;
        this.whitePlayerDto = whitePlayerDto;
        this.blackPlayerDto = blackPlayerDto;
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
        Game game = new Game(null, null, null, null, null);

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

        boolean capture = board[move.toRow][move.toColumn] != null || move.enPassant;
        boolean castleKingSide = move.castle && move.toColumn == 6;
        boolean castleQueenSide = move.castle && move.toColumn == 2;

        boolean addRowIndex = false;
        boolean addColumnIndex = false;

        if (!copy) {
            boolean[] dis = applyDisambiguation(move, piece.type);
            addRowIndex = dis[0];
            addColumnIndex = dis[1];
        }

        Piece.Type promotionType = null;
        boolean check = false;
        boolean checkmate = false;

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

            if (!copy) {
                Piece.Type type = PromotionDialog.choosePromotion(piece.white);
                board[move.toRow][move.toColumn] = new Piece(type,piece.white);
                promotionType = type;
            } else {
                board[move.toRow][move.toColumn] = new Piece(Piece.Type.QUEEN,piece.white);
            }
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
                    checkmate = true;
                    String san = buildSAN(move, piece, capture,
                            castleKingSide, castleQueenSide,
                            promotionType, check, checkmate,
                            addRowIndex, addColumnIndex);

                    history.add(san);
                    if(whiteTurn) {
                        result = "1-0";
                    } else {
                        result = "0-1";
                    }
                    return ChessResult.CHECKMATE;
                } else {
                    String san = buildSAN(move, piece, capture,
                            castleKingSide, castleQueenSide,
                            promotionType, check, checkmate,
                            addRowIndex, addColumnIndex);

                    history.add(san);
                    result = "1/2-1/2";
                    return ChessResult.STALEMATE;
                }
            } else {
                if (MoveGenerator.inCheck(this, whiteTurn)) {
                    check = true;
                    String san = buildSAN(move, piece, capture,
                            castleKingSide, castleQueenSide,
                            promotionType, check, checkmate,
                            addRowIndex, addColumnIndex);

                    history.add(san);
                    return ChessResult.CHECK;
                } else {
                    String san = buildSAN(move, piece, capture,
                            castleKingSide, castleQueenSide,
                            promotionType, check, checkmate,
                            addRowIndex, addColumnIndex);

                    history.add(san);
                    return ChessResult.NONE;
                }
            }
        }
        return null;
    }

    private boolean[] applyDisambiguation(Move move, Piece.Type type) {

        boolean addRowIndex = false;
        boolean addColumnIndex = false;

        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {

                Piece piece = board[row][column];
                if (piece == null) continue;
                if (piece.white != whiteTurn) continue;
                if (piece.type != type) continue;

                List<Move> moves = MoveGenerator.generateLegal(this, row, column);
                for (Move other : moves) {

                    if (other.toRow == move.toRow && other.toColumn == move.toColumn) {

                        if (row == move.fromRow && column == move.fromColumn) continue;

                        if (column != move.fromColumn) {
                            addColumnIndex = true;
                        } else {
                            addRowIndex = true;
                        }
                    }
                }
            }
        }

        return new boolean[]{addRowIndex, addColumnIndex};
    }

    private String buildSAN(Move move, Piece piece, boolean capture,
                            boolean castleKingSide, boolean castleQueenSide,
                            Piece.Type promotionType,
                            boolean check, boolean checkmate,
                            boolean addRowIndex, boolean addColumnIndex) {

        if (castleKingSide) return "O-O";
        if (castleQueenSide) return "O-O-O";

        StringBuilder builder = new StringBuilder();

        if (piece.type != Piece.Type.PAWN) {
            builder.append(letter(piece.type));

            if (addRowIndex && addColumnIndex) {
                builder.append((char) ('a' + move.fromColumn));
                builder.append(8 - move.fromRow);
            } else if (addRowIndex) {
                builder.append(8 - move.fromRow);
            } else if (addColumnIndex) {
                builder.append((char) ('a' + move.fromColumn));
            }
        }

        if (capture) {
            if (piece.type == Piece.Type.PAWN) {
                builder.append((char) ('a' + move.fromColumn));
            }
            builder.append("x");
        }

        builder.append((char) ('a' + move.toColumn));
        builder.append(8 - move.toRow);

        if (promotionType != null) {
            builder.append("=").append(letter(promotionType));
        }

        if (checkmate) {
            builder.append("#");
        } else if (check) {
            builder.append("+");
        }

        return builder.toString();
    }

    private String letter(Piece.Type t) {
        return switch (t) {
            case KING -> "K";
            case QUEEN -> "Q";
            case ROOK -> "R";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            default -> "";
        };
    }

    public void win(boolean whiteWins){
        if(whiteWins) {
            result = "1-0";
        } else {
            result = "0-1";
        }
    }

    public void draw(){
        result = "1/2-1/2";
    }

}
