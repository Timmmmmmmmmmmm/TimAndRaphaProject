package BoardGui;

import java.util.ArrayList;
import java.util.List;

public class MoveGenerator {
    public List<Move> generate(Game game, int row, int column) {
        Piece piece = game.board[row][column];
        if (piece == null) return null;

        return switch (piece.type) {
            case PAWN -> pawnMoves(game, row, column);
            case KNIGHT -> knightMoves(game, row, column);
            case BISHOP -> bishopMoves(game, row, column);
            case ROOK -> rookMoves(game, row, column);
            case QUEEN -> queenMoves(game, row, column);
            case KING -> kingMoves(game, row, column);
        };
    }

    static public List<Move> pawnMoves(Game game, int row, int column) {
        List<Move> moves = new ArrayList<>();
        return moves;
    }

    static public List<Move> knightMoves(Game game, int row, int column) {
        List<Move> moves = new ArrayList<>();
        return moves;
    }

    static public List<Move> bishopMoves(Game game, int row, int column) {
        List<Move> moves = new ArrayList<>();
        return moves;
    }

    static public List<Move> rookMoves(Game game, int row, int column) {
        List<Move> moves = new ArrayList<>();
        for (int i = 0;i < 7;i++) {

        }
        return moves;
    }

    static public List<Move> queenMoves(Game game, int row, int column) {
        List<Move> moves = new ArrayList<>();
        return moves;
    }

    static public List<Move> kingMoves(Game game, int row, int column) {
        List<Move> moves = new ArrayList<>();
        return moves;
    }

    static void slide(Game game, int row, int column, List<Move> moves, int[][] directions) {
        Piece piece = game.board[row][column];
        
        for (int[] direction : directions) {
            int newRow = row + direction[0];
            int newColumn = column + direction[1];

            while (inBoard(newRow, newColumn)) {
                Piece t = game.board[newRow][newColumn];
                if (t == null) {
                    moves.add(new Move(row, column, newRow, newColumn));
                } else {
                    if (t.white != piece.white) moves.add(new Move(row, column, newRow, newColumn));
                    break;
                }
                newRow += direction[0];
                newColumn += direction[1];
            }
        }
    }

    static boolean inBoard(int row, int column) {
        return row >= 0 && row < 8 && column >= 0 && column < 8;
    }
}
