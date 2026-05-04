package gui.guest;

import gui.BaseWindow;
import gui.panel.StartPanel;
import gui.util.GameResult;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class GuestResultDialog {

    public static void show(GameResult result, boolean whiteWins) {

        String[] options = {
                "Close"
        };

        JOptionPane pane = new JOptionPane(
                GuestResultDialog.getMessage(result, whiteWins),
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                GuestResultDialog.getIcon(result, whiteWins),
                options,
                options[0]
        );

        JDialog dialog = pane.createDialog(BaseWindow.getInstance(), "Game result");
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setVisible(true);
        dialog.setIconImage(null);

        BaseWindow.getInstance().setContentPane(new StartPanel());
        BaseWindow.getInstance().revalidate();
        BaseWindow.getInstance().repaint();
    }

    private static ImageIcon getIcon(GameResult result, boolean whiteWins) {
        String name = "";
        switch (result) {
            case CHECKMATE -> name = whiteWins ? "checkmate_white" : "checkmate_black";
            case RESIGN -> name = whiteWins ? "resign_white" : "resign_black";
            case TIME -> name = whiteWins ? "time_time" : "time_black";
            case DRAW -> name = "draw";
            case STALEMATE -> name = "stalemate";
            case DEAD_POSITION -> name = "dead";
            case FIVEFOLD_REPETITION_RULE -> name = "fivefold";
            case SEVENTY_FIVE_MOVE_RULE -> name = "seventyfivemove";
            case THREEFOLD_REPETITION_RULE -> name = "threefold";
            case FIFTY_MOVE_RULE -> name = "fiftymove";
        }
        ImageIcon icon = new ImageIcon(
                Objects.requireNonNull(
                        GuestResultDialog.class.getResource("/gui/assets/result/" + name + ".png")
                )
        );

        Image scaled = icon.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);

        return new ImageIcon(scaled);    }

    private static String getMessage(GameResult result, boolean whiteWins) {
        String name = "";
        switch (result) {
            case CHECKMATE -> name = whiteWins ? "White won by checkmate" : "Black won by checkmate";
            case RESIGN -> name = whiteWins ? "White won by resignation" : "Black won by resignation";
            case TIME -> name = whiteWins ? "White won on time" : "Black won on time";
            case DRAW -> name = "Draw by agreement";
            case STALEMATE -> name = "Draw by stalemate";
            case DEAD_POSITION -> name = "Draw by insufficient material";
            case FIVEFOLD_REPETITION_RULE -> name = "Draw by fivefold repetition";
            case SEVENTY_FIVE_MOVE_RULE -> name = "Draw by seventy-five-move rule";
            case THREEFOLD_REPETITION_RULE -> name = "Draw by threefold repetition";
            case FIFTY_MOVE_RULE -> name = "Draw by fifty-move rule";
        }
        return name;
    }
}