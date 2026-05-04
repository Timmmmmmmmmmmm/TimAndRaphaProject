package gui.dialog;

import gui.BaseWindow;
import gui.dto.GameInitDto;
import gui.panel.BoardPanel;
import gui.host.HostBoardPanel;
import gui.util.Game;
import gui.DatabaseConnection;
import gui.dto.GameDto;
import gui.dto.GameRoundPlayerDto;
import gui.dto.TournamentDto;
import gui.util.Move;
import gui.host.HostNetworkManager;
import gui.util.PGNReader;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class StartGameDialog {

    public static void show(TournamentDto tournamentDto, GameRoundPlayerDto selectedGame) {
        JDialog dialog = new JDialog((Frame) null, "Start game", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        String whitePlayerText = selectedGame.whitePlayer().firstname + " " + selectedGame.whitePlayer().lastname + " (" + selectedGame.whitePlayer().fide_rating + ")";
        JLabel whitePlayer = new JLabel(whitePlayerText, new ImageIcon(Objects.requireNonNull(StartGameDialog.class.getResource("/gui/assets/pieces/wK.png"))), SwingConstants.CENTER);
        whitePlayer.setFont(whitePlayer.getFont().deriveFont(18f));
        whitePlayer.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel vsLabel = new JLabel("VS", SwingConstants.CENTER);
        vsLabel.setFont(vsLabel.getFont().deriveFont(28f));
        vsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String blackPlayerText = selectedGame.blackPlayer().firstname + " " + selectedGame.blackPlayer().lastname + " (" + selectedGame.blackPlayer().fide_rating + ")";
        JLabel blackPlayer = new JLabel(blackPlayerText, new ImageIcon(Objects.requireNonNull(StartGameDialog.class.getResource("/gui/assets/pieces/bK.png"))), SwingConstants.CENTER);
        blackPlayer.setFont(blackPlayer.getFont().deriveFont(18f));
        blackPlayer.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField boardField = new JTextField();
        boardField.setMaximumSize(new Dimension(100, 30));
        boardField.setHorizontalAlignment(JTextField.CENTER);

        ((AbstractDocument) boardField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("\\d+")) super.insertString(fb, offset, string, attr);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("\\d*")) super.replace(fb, offset, length, text, attrs);
            }
        });

        JButton startButton = new JButton("Start game");
        JButton importButton = new JButton("Import game");
        JButton multiplayerButton = new JButton("Play Multiplayer");
        JButton cancelButton = new JButton("Cancel");

        startButton.setEnabled(false);
        multiplayerButton.setEnabled(false);
        importButton.setEnabled(false);

        boardField.getDocument().addDocumentListener(new DocumentListener() {
            private void update() {
                boolean valid = boardField.getText().matches("\\d+");
                startButton.setEnabled(valid);
                multiplayerButton.setEnabled(valid);
                importButton.setEnabled(valid);
            }

            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
        });

        startButton.addActionListener(_ -> {
            int boardNumber = Integer.parseInt(boardField.getText());
            GameDto gameDto = selectedGame.gameDto();
            gameDto.board_number = boardNumber;

            DatabaseConnection.executeSql("UPDATE games SET board_number = " + gameDto.board_number + " , start = '" + LocalDateTime.now() + "' WHERE id = " + gameDto.id);

            BaseWindow.getInstance().setContentPane(
                    new BoardPanel(new Game(
                            tournamentDto,
                            selectedGame.roundDto(),
                            gameDto,
                            selectedGame.whitePlayer(),
                            selectedGame.blackPlayer()
                    ), true)
            );
            BaseWindow.getInstance().revalidate();
            BaseWindow.getInstance().repaint();
            dialog.dispose();
        });

        multiplayerButton.addActionListener(_ -> {

            JTextField portField = new JTextField("5000");

            int result = JOptionPane.showConfirmDialog(
                    BaseWindow.getInstance(),
                    new Object[]{"Port:", portField},
                    "Host Game",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (result != JOptionPane.OK_OPTION) return;

            JDialog waitingDialog = new JDialog((Frame) null, "IP: " + getLocalIp(), true);
            waitingDialog.setLayout(new BorderLayout());
            waitingDialog.add(new JLabel("Waiting for client...", SwingConstants.CENTER), BorderLayout.CENTER);
            waitingDialog.setSize(250, 120);
            waitingDialog.setLocationRelativeTo(BaseWindow.getInstance());

            try {
                HostNetworkManager network = new HostNetworkManager();

                network.setOnConnected(() -> SwingUtilities.invokeLater(() -> {
                    waitingDialog.dispose();
                    dialog.dispose();

                    Game game = new Game(
                            tournamentDto,
                            selectedGame.roundDto(),
                            selectedGame.gameDto(),
                            selectedGame.whitePlayer(),
                            selectedGame.blackPlayer()
                    );

                    BaseWindow.getInstance().setContentPane(new HostBoardPanel(network, game));
                    BaseWindow.getInstance().revalidate();
                    BaseWindow.getInstance().repaint();
                }));

                new Thread(() -> {
                    try {
                        network.startServer(Integer.parseInt(portField.getText()));
                        network.sendInit(new GameInitDto(
                                tournamentDto.base_consider_time(),
                                tournamentDto.move_consider_time(),
                                selectedGame.whitePlayer().firstname,
                                selectedGame.whitePlayer().lastname,
                                selectedGame.whitePlayer().fide_rating,
                                selectedGame.whitePlayer().fide_title,
                                selectedGame.blackPlayer().firstname,
                                selectedGame.blackPlayer().lastname,
                                selectedGame.blackPlayer().fide_rating,
                                selectedGame.blackPlayer().fide_title
                        ));
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(dialog, "Start fehlgeschlagen");
                    }
                }).start();

                waitingDialog.setVisible(true);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(dialog, "Start fehlgeschlagen");
            }
        });

        importButton.addActionListener(_ -> {
            int boardNumber = Integer.parseInt(boardField.getText());
            GameDto gameDto = selectedGame.gameDto();
            gameDto.board_number = boardNumber;

            Game game = new Game(tournamentDto, selectedGame.roundDto(), gameDto, selectedGame.whitePlayer(), selectedGame.blackPlayer());
            List<Move> moves = PGNReader.readMoves(game);

            if (moves != null && !moves.isEmpty()) {
                DatabaseConnection.executeSql("UPDATE games SET board_number = " + gameDto.board_number + " , start = '" + LocalDateTime.now() + "' WHERE id = " + gameDto.id);

                BaseWindow.getInstance().setContentPane(
                        new BoardPanel(game, moves)
                );
                BaseWindow.getInstance().revalidate();
                BaseWindow.getInstance().repaint();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Error while trying to parse PGN");
            }
        });

        cancelButton.addActionListener(_ -> dialog.dispose());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel boardLabel = new JLabel("Board number:");
        boardLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        boardField.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(whitePlayer);
        panel.add(Box.createVerticalStrut(10));
        panel.add(vsLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(blackPlayer);
        panel.add(Box.createVerticalStrut(20));
        panel.add(boardLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(boardField);
        panel.add(Box.createVerticalStrut(20));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(startButton);
        buttonPanel.add(multiplayerButton);
        buttonPanel.add(importButton);
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel);

        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(BaseWindow.getInstance());
        dialog.setVisible(true);
    }

    public static String getLocalIp() {
        try {
            java.util.Enumeration<java.net.NetworkInterface> nets = java.net.NetworkInterface.getNetworkInterfaces();

            while (nets.hasMoreElements()) {
                java.net.NetworkInterface netIf = nets.nextElement();

                if (!netIf.isUp() || netIf.isLoopback()) continue;

                java.util.Enumeration<java.net.InetAddress> addresses = netIf.getInetAddresses();

                while (addresses.hasMoreElements()) {
                    java.net.InetAddress address = addresses.nextElement();

                    if (address instanceof java.net.Inet4Address && !address.isLoopbackAddress()) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (Exception ignored) {}

        return "127.0.0.1";
    }
}