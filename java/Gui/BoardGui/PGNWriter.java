package Gui.BoardGui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class PGNWriter {

    public static void export(Game game) {

        JFileChooser chooser = new JFileChooser(System.getProperty("user.home") + "/Downloads");
        chooser.setSelectedFile(new File("game.pgn"));
        chooser.setFileFilter(new FileNameExtensionFilter("PGN Dateien (*.pgn)", "pgn"));

        if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) return;
        File file = chooser.getSelectedFile();
        if (!file.getName().endsWith(".pgn")) file = new File(file.getAbsolutePath() + ".pgn");

        try {
            FileWriter writer = new FileWriter(file);
            writeHeader(writer, game);
            writeMoves(writer, game);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeHeader(FileWriter writer, Game game) throws IOException {
        writer.write("[Event \"" + game.tournamentDto.name + "\"]\n");
        writer.write("[Site \"" + game.tournamentDto.city + "\"]\n");
        writer.write("[Date \"" + game.tournamentDto.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + "\"]\n");
        writer.write("[Round \"" + game.roundDto.round_number + "\"]\n");
        writer.write("[White \"" + game.whitePlayerDto.lastname + ", " + game.whitePlayerDto.firstname + "\"]\n");
        writer.write("[Black \"" + game.blackPlayerDto.lastname + ", " + game.blackPlayerDto.firstname + "\"]\n");
        writer.write("[WhiteElo \"" + game.whitePlayerDto.fide_rating + "\"]\n");
        writer.write("[BlackElo \"" + game.blackPlayerDto.fide_rating + "\"]\n");
        writer.write("[WhiteTitle \"" + game.whitePlayerDto.fide_title + "\"]\n");
        writer.write("[BlackTitle \"" + game.blackPlayerDto.fide_title + "\"]\n");
        writer.write("[TimeControl \"" + game.tournamentDto.base_consider_time + "+" + game.tournamentDto.move_consider_time + "\"]\n");
        writer.write("[Result \"" + game.result + "\"]\n\n");
    }

    private static void writeMoves(FileWriter writer, Game game) throws IOException {

        int moveNumber = 1;

        for (int i = 0; i < game.history.size(); i++) {

            HistoryMove h = game.history.get(i);

            if (i % 2 == 0) {
                writer.write(moveNumber++ + ". ");
            }

            writer.write(toSAN(h) + " ");

            if (i % 12 == 11) {
                writer.write("\n");
            }
        }

        writer.write(game.result);
    }

    private static String toSAN(HistoryMove historyMove) {

        if (historyMove.castleKingSide) return "O-O";
        if (historyMove.castleQueenSide) return "O-O-O";

        StringBuilder builder = new StringBuilder();

        if (historyMove.piece != Piece.Type.PAWN) {
            builder.append(letter(historyMove.piece));

            if (historyMove.addRowIndex && historyMove.addColumnIndex) {
                builder.append((char) ('a' + historyMove.fromColumn));
                builder.append(8 - historyMove.fromRow);
            } else if (historyMove.addRowIndex) {
                builder.append(8 - historyMove.fromRow);
            } else if (historyMove.addColumnIndex) {
                builder.append((char) ('a' + historyMove.fromColumn));
            }
        }

        if (historyMove.capture) {
            if (historyMove.piece == Piece.Type.PAWN) {
                builder.append((char) ('a' + historyMove.fromColumn));
            }
            builder.append("x");
        }

        builder.append((char) ('a' + historyMove.toColumn));
        builder.append(8 - historyMove.toRow);

        if (historyMove.promotionType != null) {
            builder.append("=").append(letter(historyMove.promotionType));
        }

        if (historyMove.checkmate) {
            builder.append("#");
        } else if (historyMove.check) {
            builder.append("+");
        }

        return builder.toString();
    }

    private static String letter(Piece.Type t) {
        return switch (t) {
            case KING -> "K";
            case QUEEN -> "Q";
            case ROOK -> "R";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            default -> "";
        };
    }
}