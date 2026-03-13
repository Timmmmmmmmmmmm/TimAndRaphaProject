package Gui;

import Gui.BoardGui.BoardPanel;
import Gui.BoardGui.Game;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class BaseWindow extends JFrame {

    private static BaseWindow instance;

    public BaseWindow() {
        instance = this;

        setTitle("Schach-Turnierverwaltung");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setContentPane(new BoardPanel(new Game()));
        setMinimumSize(new Dimension(400, 400));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("icon.png")));
        setIconImage(icon.getImage());

        setVisible(true);
    }

    public static BaseWindow getInstance() {
        return instance;
    }

    public void setPanel(JPanel panel) {
        setContentPane(panel);
        revalidate();
        repaint();
    }

    static void main() {
        SwingUtilities.invokeLater(BaseWindow::new);
    }
}
