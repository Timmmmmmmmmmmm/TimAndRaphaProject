package gui.util;

import java.util.ArrayList;
import java.util.List;

public class MoveGenerator {

    static int enPassantColumn = -1;

    public static List<Move> generateLegal(SimpleGame game, int row, int column) {
        List<Move> moves = generate(game, row, column);
        if (moves == null || moves.isEmpty()) {
            return new ArrayList<>();
        }
        List<Move> legal = new ArrayList<>();
        for (Move move : moves) {

            SimpleGame copy = game.copy();
            copy.makeMove(move, true);

            if (!inCheck(copy, !copy.whiteTurn))
                legal.add(move);
        }

        return legal;
    }

    public static List<Move> generate(SimpleGame game, int row, int column) {
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

    static public List<Move> pawnMoves(SimpleGame game, int row, int column) {
        List<Move> moves = new ArrayList<>();
        Piece piece = game.board[row][column];
        int direction = piece.white ? -1 : 1;
        int newRow = row + direction;

        if (inBoard(newRow, column) && game.board[newRow][column] == null) {
            Move move = new Move(row, column, newRow, column);

            if (newRow == 0 || newRow == 7) {
                move.promotion = true;
            }
            moves.add(move);

            if (!piece.moved) {

                int newRow2 = row + direction * 2;

                if (game.board[newRow2][column] == null) {
                    moves.add(new Move(row, column, newRow2, column));
                }
            }
        }

        for (int dc = -1; dc <= 1; dc += 2) {
            int newColumn = column + dc;
            if (!inBoard(newRow, newColumn)) continue;

            Piece t = game.board[newRow][newColumn];
            if (t != null && t.white != piece.white) {
                Move move = new Move(row, column, newRow, newColumn);

                if (newRow == 0 || newRow == 7) {
                    move.promotion = true;
                }
                moves.add(move);
            }
        }

        if (piece.white && row == 3) {
            if (column > 0) {
                Piece right = game.board[row][column - 1];
                if (right != null && right.type == Piece.Type.PAWN && !right.white) {
                    Move move = new Move(row, column, row + direction, column - 1);
                    move.enPassant = true;

                    if (enPassantColumn == column - 1) {
                        moves.add(move);
                    }
                }
            }

            if (column < 7) {
                Piece left = game.board[row][column + 1];
                if (left != null && left.type == Piece.Type.PAWN && !left.white) {
                    Move move = new Move(row, column, row + direction, column + 1);
                    move.enPassant = true;

                    if (enPassantColumn == column + 1) {
                        moves.add(move);
                    }
                }
            }
        }

        if (!piece.white && row == 4) {
            if (column > 0) {
                Piece right = game.board[row][column - 1];
                if (right != null && right.type == Piece.Type.PAWN && right.white) {
                    Move move = new Move(row, column, row + direction, column - 1);
                    move.enPassant = true;
                    if (enPassantColumn == column - 1) {
                        moves.add(move);
                    }
                }
            }
            if (column < 7) {
                Piece left = game.board[row][column + 1];
                if (left != null && left.type == Piece.Type.PAWN && left.white) {
                    Move move = new Move(row, column, row + direction, column + 1);
                    move.enPassant = true;
                    if (enPassantColumn == column + 1) {
                        moves.add(move);
                    }
                }
            }
        }
        return moves;
    }

    static public List<Move> knightMoves(SimpleGame game, int row, int column) {
        List<Move> moves = new ArrayList<>();
        int[][] d = {
                {1, 2}, {2, 1}, {2, -1}, {1, -2},
                {-1, -2}, {-2, -1}, {-2, 1}, {-1, 2}
        };

        Piece piece = game.board[row][column];
        for (int[] a : d) {
            int newRow = row + a[0];
            int newColumn = column + a[1];

            if (!inBoard(newRow, newColumn)) continue;
            Piece oldPiece = game.board[newRow][newColumn];

            if (oldPiece == null || oldPiece.white != piece.white) {
                moves.add(new Move(row, column, newRow, newColumn));
            }
        }
        return moves;
    }

    static public List<Move> bishopMoves(SimpleGame game, int row, int column) {
        return new ArrayList<>(slide(game, row, column, new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}}));
    }

    static public List<Move> rookMoves(SimpleGame game, int row, int column) {
        return new ArrayList<>(slide(game, row, column, new int[][]{{1, 0}, {-1, -0}, {0, 1}, {0, -1}}));
    }

    static public List<Move> queenMoves(SimpleGame game, int row, int column) {
        return new ArrayList<>(slide(game, row, column, new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}, {1, 0}, {-1, -0}, {0, 1}, {0, -1}}));
    }

    static public List<Move> kingMoves(SimpleGame game, int row, int column) {
        List<Move> moves = new ArrayList<>();
        Piece piece = game.board[row][column];

        for (int dr = -1; dr <= 1; dr++)
            for (int dc = -1; dc <= 1; dc++) {
                int newRow = row + dr;
                int newColumn = column + dc;

                if (dr == 0 && dc == 0) continue;
                if (!inBoard(newRow, newColumn)) continue;

                Piece destinationPiece = game.board[newRow][newColumn];

                if (destinationPiece == null || destinationPiece.white != piece.white) {
                    moves.add(new Move(row, column, newRow, newColumn));
                }
            }

        if (!piece.moved) {
            if (canCastle(game, row, column, true)) {
                moves.add(castleMove(row, column, true));
            }

            if (canCastle(game, row, column, false)) {
                moves.add(castleMove(row, column, false));
            }
        }
        return moves;
    }

    static boolean canCastle(SimpleGame game, int row, int column, boolean kingSide) {
        Piece king = game.board[row][column];
        if (king.moved) return false;

        int rookCol = kingSide ? 7 : 0;
        Piece rook = game.board[row][rookCol];

        if (rook == null || rook.moved) return false;
        int dir = kingSide ? 1 : -1;

        for (int col = column + dir; col != rookCol; col += dir) {
            if (game.board[row][col] != null) return false;
        }

        if (isSquareAttacked(game, row, column, !king.white)) return false;

        for (int i = 1; i <= 2; i++) {
            int newCol = column + dir * i;
            if (isSquareAttacked(game, row, newCol, !king.white)) return false;
        }

        for (int i = 1; i <= 2; i++) {
            int newCol = column + dir * i;

            SimpleGame copy = game.copy();
            copy.makeMove(new Move(row, column, row, newCol), true);

            if (inCheck(copy, king.white)) return false;
        }

        return true;
    }

    static Move castleMove(int row, int column, boolean kingSide) {
        Move move;
        if (kingSide) {
            move = new Move(row, column, row, column + 2);
        } else {
            move = new Move(row, column, row, column - 2);
        }

        move.castle = true;
        return move;
    }

    static List<Move> slide(SimpleGame game, int row, int column, int[][] directions) {
        List<Move> moves = new ArrayList<>();
        Piece piece = game.board[row][column];

        for (int[] direction : directions) {
            int newRow = row + direction[0];
            int newColumn = column + direction[1];

            while (inBoard(newRow, newColumn)) {
                Piece destinationPiece = game.board[newRow][newColumn];
                if (destinationPiece == null) {
                    moves.add(new Move(row, column, newRow, newColumn));
                } else {
                    if (destinationPiece.white != piece.white) moves.add(new Move(row, column, newRow, newColumn));
                    break;
                }
                newRow += direction[0];
                newColumn += direction[1];
            }
        }
        return moves;
    }

    static boolean inBoard(int row, int column) {
        return row >= 0 && row < 8 && column >= 0 && column < 8;
    }

    static boolean inCheck(SimpleGame game, boolean white) {

        int kingR = -1, kingC = -1;

        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {
                Piece p = game.board[r][c];
                if (p != null && p.type == Piece.Type.KING && p.white == white) {
                    kingR = r;
                    kingC = c;
                }
            }

        return isSquareAttacked(game, kingR, kingC, !white);
    }

    static boolean isSquareAttacked(SimpleGame game, int row, int col, boolean byWhite) {

        // === PAWNS ===
        int pawnDir = byWhite ? -1 : 1;
        int pawnRow = row + pawnDir;

        if (inBoard(pawnRow, col - 1)) {
            Piece p = game.board[pawnRow][col - 1];
            if (p != null && p.white == byWhite && p.type == Piece.Type.PAWN)
                return true;
        }

        if (inBoard(pawnRow, col + 1)) {
            Piece p = game.board[pawnRow][col + 1];
            if (p != null && p.white == byWhite && p.type == Piece.Type.PAWN)
                return true;
        }

        // === KNIGHTS ===
        int[][] knightMoves = {
                {1, 2}, {2, 1}, {2, -1}, {1, -2},
                {-1, -2}, {-2, -1}, {-2, 1}, {-1, 2}
        };

        for (int[] d : knightMoves) {
            int r = row + d[0];
            int c = col + d[1];

            if (!inBoard(r, c)) continue;

            Piece p = game.board[r][c];
            if (p != null && p.white == byWhite && p.type == Piece.Type.KNIGHT)
                return true;
        }

        // Bishop + Queen
        int[][] bishopDirs = {{1,1},{1,-1},{-1,1},{-1,-1}};
        for (int[] d : bishopDirs) {
            int r = row + d[0];
            int c = col + d[1];

            while (inBoard(r, c)) {
                Piece p = game.board[r][c];
                if (p != null) {
                    if (p.white == byWhite &&
                            (p.type == Piece.Type.BISHOP || p.type == Piece.Type.QUEEN))
                        return true;
                    break;
                }
                r += d[0];
                c += d[1];
            }
        }

        // Rook + Queen
        int[][] rookDirs = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] d : rookDirs) {
            int r = row + d[0];
            int c = col + d[1];

            while (inBoard(r, c)) {
                Piece p = game.board[r][c];
                if (p != null) {
                    if (p.white == byWhite &&
                            (p.type == Piece.Type.ROOK || p.type == Piece.Type.QUEEN))
                        return true;
                    break;
                }
                r += d[0];
                c += d[1];
            }
        }

        // === KING ===
        for (int dr = -1; dr <= 1; dr++)
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;

                int r = row + dr;
                int c = col + dc;

                if (!inBoard(r, c)) continue;

                Piece p = game.board[r][c];
                if (p != null && p.white == byWhite && p.type == Piece.Type.KING)
                    return true;
            }

        return false;
    }
}
