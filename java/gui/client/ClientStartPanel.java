package gui.client;

import gui.BaseWindow;
import gui.dto.PlayerDto;
import gui.dto.TournamentDto;
import gui.util.Game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class ClientStartPanel extends JPanel {

    private BufferedImage backgroundImage;

    public ClientStartPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        try {
            backgroundImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/gui/assets/startBackground.png")));
        } catch (Exception ignored) {
        }

        add(createMenuPanel(), BorderLayout.NORTH);

        JLabel titleLabel = new JLabel("ChessManager - Client");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        add(titleLabel, BorderLayout.CENTER);
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(0, 0, 0, 0));

        JButton joinButton = new JButton("Join Multiplayer Game");
        joinButton.addActionListener(_ -> joinGame());

        panel.add(joinButton);

        return panel;
    }

    private void joinGame() {

        JTextField ipField = new JTextField("localhost");
        JTextField portField = new JTextField("5000");

        int result = JOptionPane.showConfirmDialog(
                this,
                new Object[]{"IP:", ipField, "Port:", portField},
                "Connect to Server",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) return;

        try {
            ClientNetworkManager net = getClientNetworkManager();

            new Thread(() -> {
                try {
                    net.connect(ipField.getText(), Integer.parseInt(portField.getText()));
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this, "Verbindung fehlgeschlagen")
                    );
                }
            }).start();

        } catch (Exception e) {
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this, "Verbindung fehlgeschlagen")
            );
        }
    }

    private static ClientNetworkManager getClientNetworkManager() {
        ClientNetworkManager net = new ClientNetworkManager();

        net.setOnGameInit(dto -> SwingUtilities.invokeLater(() -> {

            if (dto.isSimple) {
                BaseWindow.getInstance().setContentPane(
                        new ClientBoardPanel(net, dto.base_consider_time, dto.move_consider_time)
                );
            } else {
                Game game = new Game(
                        new TournamentDto(0, null, null, null, dto.base_consider_time, dto.move_consider_time, null),
                        null,
                        null,
                        new PlayerDto(0, dto.whiteFirstname, dto.whiteLastname, dto.whiteRating, dto.whiteTitle, ' ', null),
                        new PlayerDto(0, dto.blackFirstname, dto.blackLastname, dto.blackRating, dto.blackTitle, ' ', null));

                BaseWindow.getInstance().setContentPane(
                        new ClientBoardPanel(net, game)
                );
            }

            BaseWindow.getInstance().revalidate();
            BaseWindow.getInstance().repaint();
        }));
        return net;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            int w = getWidth();
            int h = getHeight();

            int iw = backgroundImage.getWidth(this);
            int ih = backgroundImage.getHeight(this);

            double scale = Math.max((double) w / iw, (double) h / ih);

            int nw = (int) (iw * scale);
            int nh = (int) (ih * scale);

            g.drawImage(backgroundImage, (w - nw) / 2, (h - nh) / 2, nw, nh, null);
        }
    }
}