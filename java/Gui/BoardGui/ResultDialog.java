package Gui.BoardGui;

import Gui.BaseWindow;

import javax.swing.*;
import java.util.Objects;

public class ResultDialog {

    public static void showResult(Game game, GameResult result, boolean white) {

        String[] options = {
                "PGN herunterladen",
                "Schließen",
                "Neues Spiel"
        };

        JOptionPane pane = new JOptionPane(
                "The game ended as a " + result.name(),
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                new ImageIcon(Objects.requireNonNull(PromotionDialog.class.getResource("images/pieces/" + (white ? "w" : "b") + "P.png")
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
                showResult(game, result, white);
                break;
            case 1:
                break;
            case 2:
                BaseWindow.getInstance().setPanel(new BoardPanel(new Game(null, null, null, null)));
                break;
        }
    }
}