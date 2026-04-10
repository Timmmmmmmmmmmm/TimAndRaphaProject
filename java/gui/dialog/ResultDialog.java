package gui.dialog;

import gui.BaseWindow;
import gui.util.Game;
import gui.util.GameResult;
import gui.util.PGNWriter;
import gui.DatabaseConnection;
import gui.panel.TournamentPanel;

import javax.swing.*;
import java.util.Objects;

public class ResultDialog {

    public static void show(Game game, GameResult result, boolean whiteWins) {

        String[] options = {
                "PGN herunterladen",
                "Schließen"
        };

        JOptionPane pane = new JOptionPane(
                "The game ended as a " + result.name(),
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                new ImageIcon(Objects.requireNonNull(PromotionDialog.class.getResource("/gui/assets/pieces/" + (whiteWins ? "w" : "b") + "P.png")
                )),
                options,
                options[0]
        );

        JDialog dialog = pane.createDialog("Game result");
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setVisible(true);
        dialog.setIconImage(null);

        Object selectedValue = pane.getValue();
        if (selectedValue == null) return;

        int res = -1;
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(selectedValue)) {
                res = i;
                break;
            }
        }

        switch (res) {
            case 0:
                PGNWriter.export(game);
                show(game, result, whiteWins);
                break;
            case 1:
                int resultInt;
                if (result == GameResult.DRAW || result == GameResult.STALEMATE || result == GameResult.FIFTY_MOVE_RULE || result == GameResult.THREE_REPETITION_RULE) {
                    resultInt = 0;
                } else {
                    resultInt = whiteWins ? 1 : -1;
                }
                DatabaseConnection.executeSql("UPDATE games SET result = " + resultInt + " WHERE id = " + game.gameDto.id);

                double whiteScore = (resultInt == 1) ? 1.0 : (resultInt == 0 ? 0.5 : 0.0);
                double blackScore = (resultInt == -1) ? 1.0 : (resultInt == 0 ? 0.5 : 0.0);
                DatabaseConnection.executeSql("UPDATE player_tournament_info SET score = score + " + whiteScore + " WHERE player_id = " + game.whitePlayerDto.id);
                DatabaseConnection.executeSql("UPDATE player_tournament_info SET score = score + " + blackScore + " WHERE player_id = " + game.blackPlayerDto.id);
                BaseWindow window = BaseWindow.getInstance();
                window.setContentPane(new TournamentPanel(game.tournamentDto));
                window.revalidate();
                window.repaint();
                break;
        }
    }
}