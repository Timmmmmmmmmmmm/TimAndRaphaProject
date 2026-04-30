package gui.client;

import gui.BaseWindow;
import gui.dialog.PromotionDialog;
import gui.util.GameResult;

import javax.swing.*;
import java.util.Objects;

public class ClientResultDialog {

    public static void show(GameResult result, boolean whiteWins) {

        String[] options = {
                "Close"
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

        JDialog dialog = pane.createDialog(BaseWindow.getInstance(), "Game result");
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setVisible(true);
        dialog.setIconImage(null);

        BaseWindow.getInstance().setContentPane(new ClientStartPanel());
        BaseWindow.getInstance().revalidate();
        BaseWindow.getInstance().repaint();
    }
}