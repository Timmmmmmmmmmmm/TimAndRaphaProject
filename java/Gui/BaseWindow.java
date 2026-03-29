package Gui;

import Gui.BoardGui.BoardPanel;
import Gui.BoardGui.Game;
import Gui.Dto.GameDto;
import Gui.Dto.PlayerDto;
import Gui.Dto.RoundDto;
import Gui.Dto.TournamentDto;
import Gui.TournamentGui.StartGamePanel;
import Gui.TournamentGui.StartTournamentPanel;
import Gui.TournamentGui.TournamentPanel;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Objects;

public class BaseWindow extends JFrame {

    private static BaseWindow instance;

    public BaseWindow() {
        instance = this;
        applyChessTheme(this);

        setTitle("Schach-Turnierverwaltung");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setContentPane(new StartTournamentPanel());
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setMinimumSize(new Dimension(screenSize.width / 4, screenSize.height / 4));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("icon.png")));
        setIconImage(icon.getImage());

        setVisible(true);
        revalidate();
        repaint();
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
        Color accentRed = new Color(239, 81, 81, 180);
        Color textDark = new Color(40, 40, 40);

        UIManager.put("Button.background", primaryGreen);
        UIManager.put("Button.foreground", Color.BLACK);
        UIManager.put("Button.border", BorderFactory.createEmptyBorder(5, 15, 5, 15));
        UIManager.put("Button.focus", primaryDark);

        UIManager.put("Label.foreground", textDark);

        UIManager.put("Table.background", primaryLight);
        UIManager.put("Table.alternateRowColor", primaryDark);
        UIManager.put("Table.foreground", textDark);
        UIManager.put("Table.selectionBackground", accentRed);
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("Table.gridColor", new Color(200, 200, 200));
        UIManager.put("Table.rowHeight", 28);

        UIManager.put("ComboBox.background", primaryLight);
        UIManager.put("ComboBox.foreground", textDark);
        UIManager.put("ComboBox.selectionBackground", accentRed);
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
        UIManager.put("List.selectionBackground", accentRed);
        UIManager.put("List.selectionForeground", Color.WHITE);

        UIManager.put("OptionPane.background", primaryLight);
        UIManager.put("OptionPane.messageForeground", textDark);

        SwingUtilities.updateComponentTreeUI(frame);
    }
}