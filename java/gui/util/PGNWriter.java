package gui.util;

import gui.DatabaseConnection;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class PGNWriter {

    public static void export(SimpleGame game) {

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
            System.out.println("Datei konnte nicht beschrieben werden!");
        }
    }

    public static void saveMovesInDatabase(Game game) {
        for (int i = 1; i < game.history.size(); i++) {
            String san = game.history.get(i);
            DatabaseConnection.executeSql("INSERT INTO games_moves (move_number, move, games_id) VALUES (" + i + ", '" + san + "', " + game.gameDto.id + ");");
        }
    }

    private static void writeHeader(FileWriter writer, SimpleGame game) throws IOException {
        if (game instanceof Game) {
            writer.write("[Event \"" + ((Game) game).tournamentDto.name() + "\"]\n");
            writer.write("[Site \"" + ((Game) game).tournamentDto.city() + "\"]\n");
            writer.write("[Date \"" + ((Game) game).tournamentDto.date().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + "\"]\n");
            writer.write("[Round \"" + ((Game) game).roundDto.round_number() + "\"]\n");
            writer.write("[White \"" + ((Game) game).whitePlayerDto.lastname + ", " + ((Game) game).whitePlayerDto.firstname + "\"]\n");
            writer.write("[Black \"" + ((Game) game).blackPlayerDto.lastname + ", " + ((Game) game).blackPlayerDto.firstname + "\"]\n");
            writer.write("[WhiteElo \"" + ((Game) game).whitePlayerDto.fide_rating + "\"]\n");
            writer.write("[BlackElo \"" + ((Game) game).blackPlayerDto.fide_rating + "\"]\n");
            writer.write("[WhiteTitle \"" + ((Game) game).whitePlayerDto.fide_title + "\"]\n");
            writer.write("[BlackTitle \"" + ((Game) game).blackPlayerDto.fide_title + "\"]\n");
            writer.write("[TimeControl \"" + ((Game) game).tournamentDto.base_consider_time() + "+" + ((Game) game).tournamentDto.move_consider_time() + "\"]\n");
        }


        writer.write("[Result \"" + game.result + "\"]\n\n");
    }

    private static void writeMoves(FileWriter writer, SimpleGame game) throws IOException {

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