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
        writer.write("[Event \"" + game.tournament.name + "\"]\n");
        writer.write("[Site \"" + game.tournament.city + "\"]\n");
        writer.write("[Date \"" + game.tournament.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + "\"]\n");
        writer.write("[Round \"" + game.round.roundNumber + "\"]\n");
        writer.write("[White \"" + game.whitePlayer.lastname + ", " + game.whitePlayer.firstname + "\"]\n");
        writer.write("[Black \"" + game.blackPlayer.lastname + ", " + game.blackPlayer.firstname + "\"]\n");
        writer.write("[WhiteElo \"" + game.whitePlayer.fideRating + "\"]\n");
        writer.write("[BlackElo \"" + game.blackPlayer.fideRating + "\"]\n");
        writer.write("[WhiteTitle \"" + game.whitePlayer.fideTitle + "\"]\n");
        writer.write("[BlackTitle \"" + game.blackPlayer.fideTitle + "\"]\n");
        writer.write("[TimeControl \"" + game.tournament.base_consider_time + "+" + game.tournament.move_consider_time + "\"]\n");
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

    private static String toSAN(HistoryMove h) {

        if (h.castleKingSide) return "O-O";
        if (h.castleQueenSide) return "O-O-O";

        StringBuilder s = new StringBuilder();

        if (h.piece != Piece.Type.PAWN) {
            s.append(letter(h.piece));
        }

        if (h.capture) {
            if (h.piece == Piece.Type.PAWN) {
                s.append((char) ('a' + h.fromColumn));
            }
            s.append("x");
        }

        s.append((char) ('a' + h.toColumn));
        s.append(8 - h.toRow);

        if (h.promotionType != null) {
            s.append("=").append(letter(h.promotionType));
        }

        if (h.checkmate) {
            s.append("#");
        } else if (h.check) {
            s.append("+");
        }

        return s.toString();
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