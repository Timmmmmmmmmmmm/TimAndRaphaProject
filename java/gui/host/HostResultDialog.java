package gui.host;

import gui.BaseWindow;
import gui.guest.GuestResultDialog;
import gui.panel.StartPanel;
import gui.util.Game;
import gui.util.GameResult;
import gui.util.PGNWriter;
import gui.DatabaseConnection;
import gui.panel.TournamentPanel;
import gui.util.SimpleGame;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class HostResultDialog {

    public static void show(SimpleGame game, GameResult result, boolean whiteWins) {

        String[] options = {
                "Download PGN",
                "Close"
        };

        JOptionPane pane = new JOptionPane(
                HostResultDialog.getMessage(result, whiteWins),
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                HostResultDialog.getIcon(result, whiteWins),
                options,
                options[0]
        );

        JDialog dialog = pane.createDialog(BaseWindow.getInstance(), "Game result");
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
                PGNWriter.exportPGN(game);
                show(game, result, whiteWins);
                break;
            case 1:
                if (game instanceof Game) {
                    int resultInt;
                    if (result == GameResult.DRAW || result == GameResult.STALEMATE || result == GameResult.FIFTY_MOVE_RULE || result == GameResult.THREEFOLD_REPETITION_RULE) {
                        resultInt = 0;
                    } else {
                        resultInt = whiteWins ? 1 : -1;
                    }
                    DatabaseConnection.executeSql("UPDATE games SET result = " + resultInt + " WHERE id = " + ((Game) game).gameDto.id);

                    double whiteScore = (resultInt == 1) ? 1.0 : (resultInt == 0 ? 0.5 : 0.0);
                    double blackScore = (resultInt == -1) ? 1.0 : (resultInt == 0 ? 0.5 : 0.0);
                    DatabaseConnection.executeSql("UPDATE player_tournament_info SET score = score + " + whiteScore + " WHERE player_id = " + ((Game) game).whitePlayerDto.id);
                    DatabaseConnection.executeSql("UPDATE player_tournament_info SET score = score + " + blackScore + " WHERE player_id = " + ((Game) game).blackPlayerDto.id);
                    BaseWindow.getInstance().setContentPane(new TournamentPanel(((Game) game).tournamentDto));
                } else {
                    BaseWindow.getInstance().setContentPane(new StartPanel());
                }

                BaseWindow.getInstance().revalidate();
                BaseWindow.getInstance().repaint();
                break;
        }
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