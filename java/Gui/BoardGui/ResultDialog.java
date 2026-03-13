package Gui.BoardGui;

import Gui.BaseWindow;

import javax.swing.*;
import java.util.Objects;

public class ResultDialog {

    public static void showResult(Game.ChessResult result, boolean white) {
        String[] options = {
                "PGN herunterladen",
                "Schließen",
                "Neues Spiel"
        };
        int res = JOptionPane.showOptionDialog(
                null,
                "The game endet as a " + result.name(),
                "Game result",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                new ImageIcon(Objects.requireNonNull(PromotionDialog.class.getResource("images/pieces/" + (white ? "white" : "black") + "/pawn.png"))),
                options,
                options[0]
        );
        switch (res) {
            case 1:
            case 2:
            default:
                BaseWindow.getInstance().setPanel(new BoardPanel(new Game()));
                break;
        }
    }
}