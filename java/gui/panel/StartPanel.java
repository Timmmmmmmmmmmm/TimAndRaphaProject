package gui.panel;

import gui.BaseWindow;
import gui.guest.GuestBoardPanel;
import gui.guest.GuestNetworkManager;
import gui.dto.GameInitDto;
import gui.dto.PlayerDto;
import gui.dto.TournamentDto;
import gui.host.HostBoardPanel;
import gui.host.HostNetworkManager;
import gui.util.Game;
import gui.util.Move;
import gui.util.PGNReader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

public class StartPanel extends JPanel {

    private BufferedImage backgroundImage;

    private static final int DEFAULT_BASE_CONSIDER_TIME = 600;
    private static final int DEFAULT_MOVE_CONSIDER_TIME = 10;

    public StartPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        try {
            backgroundImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/gui/assets/startBackground.png")));
        } catch (Exception ignored) {
        }

        add(createMenuPanel(), BorderLayout.NORTH);

        JLabel titleLabel = new JLabel("ChessManager");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        add(titleLabel, BorderLayout.CENTER);
    }

    public JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        menuPanel.setBackground(new Color(0, 0, 0, 0));

        JButton tournamentButton = new JButton("Manage tournaments");
        tournamentButton.addActionListener(_ -> {
            BaseWindow.getInstance().setContentPane(new StartTournamentPanel());
            BaseWindow.getInstance().revalidate();
            BaseWindow.getInstance().repaint();
        });

        JButton quickGameButton = new JButton("Quick game");
        quickGameButton.addActionListener(_ -> {
            BaseWindow.getInstance().setContentPane(new BoardPanel(60, 10, true));
            BaseWindow.getInstance().revalidate();
            BaseWindow.getInstance().repaint();
        });

        JButton multiplayerButton = new JButton("Host online game");
        multiplayerButton.addActionListener(_ -> hostSimpleGame());

        JButton joinButton = new JButton("Join online game");
        joinButton.addActionListener(_ -> joinGame());

        JButton importGameButton = getImportGameButton();

        menuPanel.add(tournamentButton);
        menuPanel.add(quickGameButton);
        menuPanel.add(multiplayerButton);
        menuPanel.add(joinButton);
        menuPanel.add(importGameButton);

        return menuPanel;
    }

    private static JButton getImportGameButton() {
        JButton importGameButton = new JButton("Import game");
        importGameButton.addActionListener(_ -> {
            List<Move> moves = PGNReader.readMoves();
            if (moves != null && !moves.isEmpty()) {
                BaseWindow.getInstance().setContentPane(new BoardPanel(moves));
                BaseWindow.getInstance().revalidate();
                BaseWindow.getInstance().repaint();
            }
        });
        return importGameButton;
    }

    private void hostSimpleGame() {
        JTextField portField = new JTextField("5000");

        int result = JOptionPane.showConfirmDialog(
                this,
                new Object[]{"Port:", portField},
                "Host quick game",
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

                BaseWindow.getInstance().setContentPane(new HostBoardPanel(network, DEFAULT_BASE_CONSIDER_TIME, DEFAULT_MOVE_CONSIDER_TIME));
                BaseWindow.getInstance().revalidate();
                BaseWindow.getInstance().repaint();
            }));

            new Thread(() -> {
                try {
                    network.startServer(Integer.parseInt(portField.getText()));
                    network.sendInit(new GameInitDto(DEFAULT_BASE_CONSIDER_TIME, DEFAULT_MOVE_CONSIDER_TIME));
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Start fehlgeschlagen");
                }
            }).start();

            waitingDialog.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Start fehlgeschlagen");
        }
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
            GuestNetworkManager net = getClientNetworkManager();

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

    private static GuestNetworkManager getClientNetworkManager() {
        GuestNetworkManager net = new GuestNetworkManager();

        net.setOnGameInit(dto -> SwingUtilities.invokeLater(() -> {

            if (dto.isSimple) {
                BaseWindow.getInstance().setContentPane(
                        new GuestBoardPanel(net, dto.base_consider_time, dto.move_consider_time)
                );
            } else {
                Game game = new Game(
                        new TournamentDto(0, null, null, null, dto.base_consider_time, dto.move_consider_time, null),
                        null,
                        null,
                        new PlayerDto(0, dto.whiteFirstname, dto.whiteLastname, dto.whiteRating, dto.whiteTitle, ' ', null),
                        new PlayerDto(0, dto.blackFirstname, dto.blackLastname, dto.blackRating, dto.blackTitle, ' ', null));

                BaseWindow.getInstance().setContentPane(
                        new GuestBoardPanel(net, game)
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