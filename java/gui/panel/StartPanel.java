package gui.panel;

import gui.BaseWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.util.Objects;
import javax.imageio.ImageIO;

public class StartPanel extends JPanel {

    private BufferedImage backgroundImage;

    public StartPanel() {
        setLayout(new BorderLayout(10, 10));

        try {
            backgroundImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/gui/assets/startBackground.png")));
        } catch (Exception ignored) {
        }

        add(createMenuPanel(), BorderLayout.NORTH);

        JLabel titleLabel = new JLabel("<html>Schachturnier -<br>Verwaltung</html>");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        add(titleLabel, BorderLayout.EAST);
    }

    public JPanel createMenuPanel() {
        JButton tournamentButton = new JButton("Manage tournaments");
        tournamentButton.addActionListener(_ -> {
            BaseWindow.getInstance().setContentPane(new StartTournamentPanel());
            BaseWindow.getInstance().revalidate();
            BaseWindow.getInstance().repaint();
        });

        JButton quickGameButton = new JButton("Start quick game");
        quickGameButton.addActionListener(_ -> BaseWindow.getInstance().setContentPane(new BoardPanel(60, 10)));

        JButton importGameButton = new JButton("Import game");
        importGameButton.addActionListener(_ -> {
            // TODO Read PGN file
        });

        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        menuPanel.add(tournamentButton);
        menuPanel.add(quickGameButton);
        menuPanel.add(importGameButton);
        return menuPanel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            int panelWidth = getWidth();
            int panelHeight = getHeight();

            int imgWidth = backgroundImage.getWidth(this);
            int imgHeight = backgroundImage.getHeight(this);

            double scale = Math.max(
                    (double) panelWidth / imgWidth,
                    (double) panelHeight / imgHeight
            );

            int newWidth = (int) (imgWidth * scale);
            int newHeight = (int) (imgHeight * scale);

            int x = (panelWidth - newWidth) / 2;
            int y = (panelHeight - newHeight) / 2;

            g.drawImage(backgroundImage, x, y, newWidth, newHeight, this);        }
    }
}