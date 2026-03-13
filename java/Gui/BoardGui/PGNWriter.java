package Gui.BoardGui;

import javax.swing.*;
import java.io.FileWriter;
import java.time.format.DateTimeFormatter;

public class PGNWriter {

    public static void export(Game game) {

        JFileChooser chooser = new JFileChooser();

        if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) return;

        try {

            FileWriter writer = new FileWriter(chooser.getSelectedFile());

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

            int moveNumber = 1;

            for (int i = 0; i < game.history.size(); i++) {

                Move m = game.history.get(i);

                if (i % 2 == 0) {

                    writer.write(moveNumber + ". ");
                    moveNumber++;
                }

                char file =
                        (char) ('a' + m.toColumn);

                int rank =
                        8 - m.toRow;

                writer.write(file + "" + rank + " ");
            }

            writer.write(game.result);

            writer.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}