package Gui.BoardGui;

import Gui.DatabaseConnection;

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
        chooser.setFileFilter(new FileNameExtensionFilter("PGN files (*.pgn)", "pgn"));

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

    public static void saveMovesInDatabase(Game game) {
        for (int i = 1; i < game.history.size(); i++) {
            String san = game.history.get(i);
            DatabaseConnection.executeSql("INSERT INTO games_moves (move_number, move, games_id) VALUES (" + i + ", '" + san + "', " + game.gameDto.id + ");");
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

            String san = game.history.get(i);

            if (i % 2 == 0) {
                writer.write(moveNumber++ + ". ");
            }

            writer.write(san + " ");

            if (i % 12 == 11) {
                writer.write("\n");
            }
        }

        writer.write(game.result);
    }
}