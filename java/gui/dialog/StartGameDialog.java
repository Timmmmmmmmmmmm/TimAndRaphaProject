package gui.dialog;

import gui.BaseWindow;
import gui.panel.BoardPanel;
import gui.util.Game;
import gui.DatabaseConnection;
import gui.dto.GameDto;
import gui.dto.GameRoundPlayerDto;
import gui.dto.TournamentDto;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.Objects;

public class StartGameDialog {

    public static void show(TournamentDto tournamentDto, GameRoundPlayerDto selectedGame) {
        String whitePlayerText = selectedGame.whitePlayer().firstname + " " + selectedGame.whitePlayer().lastname + " (" + selectedGame.whitePlayer().fide_rating + ")";
        JLabel whitePlayer = new JLabel(whitePlayerText, new ImageIcon(Objects.requireNonNull(StartGameDialog.class.getResource("/gui/assets/pieces/wK.png"))), SwingConstants.CENTER);
        whitePlayer.setFont(whitePlayer.getFont().deriveFont(18f));
        JLabel vsLabel = new JLabel("VS", SwingConstants.CENTER);
        vsLabel.setFont(vsLabel.getFont().deriveFont(28f));
        String blackPlayerText = selectedGame.blackPlayer().firstname + " " + selectedGame.blackPlayer().lastname + " (" + selectedGame.blackPlayer().fide_rating + ")";
        JLabel backPlayer = new JLabel(blackPlayerText, new ImageIcon(Objects.requireNonNull(StartGameDialog.class.getResource("/gui/assets/pieces/bK.png"))), SwingConstants.CENTER);
        backPlayer.setFont(backPlayer.getFont().deriveFont(18f));
        JTextField boardField = new JTextField();
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(whitePlayer);
        panel.add(Box.createVerticalStrut(10));
        panel.add(vsLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(backPlayer);
        panel.add(Box.createVerticalStrut(20));

        panel.add(new JLabel("Board number:"));
        panel.add(boardField);

        int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Start game",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            GameDto gameDto = selectedGame.gameDto();
            gameDto.board_number = Integer.parseInt(boardField.getText());

            DatabaseConnection.executeSql("UPDATE games SET board_number = " + gameDto.board_number + " , start = '" + LocalDateTime.now() + "' WHERE id = " + gameDto.id);

            BaseWindow.getInstance().setContentPane(
                    new BoardPanel(new Game(
                            tournamentDto,
                            selectedGame.roundDto(),
                            gameDto,
                            selectedGame.whitePlayer(),
                            selectedGame.blackPlayer()
                    ))
            );
            BaseWindow.getInstance().revalidate();
        }
    }
}
