package gui;

import gui.panel.StartPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import java.util.Random;

public class BaseWindow extends JFrame {

    private static BaseWindow instance;

    public BaseWindow() {
        instance = this;
        applyChessTheme(this);

        setTitle("Schach-Turnierverwaltung");
        setContentPane(new StartPanel());
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int) (screenSize.width * 0.65), (int) (screenSize.height * 0.65));
        setMinimumSize(new Dimension(screenSize.width / 4, screenSize.height / 4));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);
        setVisible(true);
        revalidate();
        repaint();

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/gui/assets/analysis/brilliant.png")));
        setIconImage(icon.getImage());

        Random random = new Random();
        Timer timer = new Timer(10000, new ActionListener() {
            String lastAnalysis = "brilliant";
            @Override
            public void actionPerformed(ActionEvent e) {
                String analysis = switch (random.nextInt(8)) {
                    case 0 -> lastAnalysis.equals("best") ? "blunder" : "best";
                    case 1 -> lastAnalysis.equals("blunder") ? "brilliant" : "blunder";
                    case 2 -> lastAnalysis.equals("brilliant") ? "critical" : "brilliant";
                    case 3 -> lastAnalysis.equals("critical") ? "excellent" : "critical";
                    case 4 -> lastAnalysis.equals("excellent") ? "inaccuracy" : "excellent";
                    case 5 -> lastAnalysis.equals("inaccuracy") ? "mistake" : "inaccuracy";
                    case 6 -> lastAnalysis.equals("mistake") ? "okay" : "mistake";
                    case 7 -> lastAnalysis.equals("okay") ? "theory" : "okay";
                    default -> lastAnalysis.equals("theory") ? "best" : "theory";
                };
                lastAnalysis = analysis;
                ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/gui/assets/analysis/" + analysis + ".png")));
                setIconImage(icon.getImage());
            }
        });
        timer.start();
    }

    public static BaseWindow getInstance() {
        return instance;
    }

    static void main() {
        SwingUtilities.invokeLater(BaseWindow::new);
    }

    public void applyChessTheme(JFrame frame) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        Color primaryGreen = new Color(80, 124, 101);
        Color primaryLight = new Color(222, 227, 230);
        Color primaryDark = new Color(140, 162, 173);
        Color textDark = new Color(40, 40, 40);

        UIManager.put("Button.background", primaryGreen);
        UIManager.put("Button.foreground", Color.BLACK);
        UIManager.put("Button.border", BorderFactory.createEmptyBorder(5, 15, 5, 15));
        UIManager.put("Button.focus", primaryDark);

        UIManager.put("Label.foreground", textDark);

        UIManager.put("Table.background", primaryLight);
        UIManager.put("Table.alternateRowColor", primaryDark);
        UIManager.put("Table.foreground", textDark);
        UIManager.put("Table.selectionBackground", primaryDark);
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("Table.gridColor", new Color(200, 200, 200));
        UIManager.put("Table.rowHeight", 28);

        UIManager.put("ComboBox.background", primaryLight);
        UIManager.put("ComboBox.foreground", textDark);
        UIManager.put("ComboBox.selectionBackground", primaryDark);
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);

        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextField.foreground", textDark);

        UIManager.put("TitledBorder.color", textDark);

        UIManager.put("Panel.background", primaryLight);
        UIManager.put("ScrollPane.background", primaryLight);
        UIManager.put("ScrollBar.thumb", primaryDark);
        UIManager.put("ScrollBar.track", primaryLight);

        UIManager.put("List.background", primaryLight);
        UIManager.put("List.foreground", textDark);
        UIManager.put("List.selectionBackground", primaryDark);
        UIManager.put("List.selectionForeground", Color.WHITE);

        UIManager.put("OptionPane.background", primaryLight);
        UIManager.put("OptionPane.messageForeground", textDark);

        SwingUtilities.updateComponentTreeUI(frame);
    }
}