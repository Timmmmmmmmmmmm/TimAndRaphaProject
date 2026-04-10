package Gui.panel;

import Gui.BaseWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;

public class StartPanel extends JPanel {
    private BufferedImage backgroundImage;
    private JButton turniereButton;
    private JButton schnellesSpielButton;
    private JButton spielImportierenButton;

    public StartPanel() {
        setLayout(new BorderLayout(10, 10));

        try {
            backgroundImage = ImageIO.read(getClass().getResource("/Gui/assets/startBackground.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        turniereButton = new JButton("Manage tournaments");
        schnellesSpielButton = new JButton("Start quick game");
        spielImportierenButton = new JButton("Import game");

        gbc.gridy = 0;
        buttonPanel.add(turniereButton, gbc);
        gbc.gridy = 1;
        buttonPanel.add(schnellesSpielButton, gbc);
        gbc.gridy = 2;
        buttonPanel.add(spielImportierenButton, gbc);

        turniereButton.addActionListener(_ -> {
            BaseWindow.getInstance().setContentPane(new StartTournamentPanel());
            BaseWindow.getInstance().revalidate();
            BaseWindow.getInstance().repaint();
        });
        schnellesSpielButton.addActionListener(e -> {
            BaseWindow.getInstance().setContentPane(new BoardPanel());
        });
        spielImportierenButton.addActionListener(e -> {
            // TODO Read PGN file
        });

        add(buttonPanel, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("<html>Schachturnier -<br>Verwaltung</html>");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new GridBagLayout());
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.EAST);
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