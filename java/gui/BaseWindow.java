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
        SwingUtilities.updateComponentTreeUI(frame);
    }
}