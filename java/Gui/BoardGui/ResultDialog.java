package Gui.BoardGui;

import javax.swing.*;
import java.util.Objects;

public class ResultDialog {

    public static void showResult(Game game, GameResult result, boolean whiteWins) {

        String[] options = {
                "PGN herunterladen",
                "Schließen"
        };

        JOptionPane pane = new JOptionPane(
                "The game ended as a " + result.name(),
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                new ImageIcon(Objects.requireNonNull(PromotionDialog.class.getResource("images/pieces/" + (whiteWins ? "w" : "b") + "P.png")
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
                showResult(game, result, whiteWins);
                break;
            case 1:
                break;
        }
    }
}