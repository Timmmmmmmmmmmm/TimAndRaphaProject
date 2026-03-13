import BoardGui.BoardPanel;
import BoardGui.Game;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class BaseWindow extends JFrame {

    public BaseWindow() {

        Game game = new Game();

        setTitle("Schach-Turnierverwaltung");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setContentPane(new BoardPanel(game));
        setMinimumSize(new Dimension(400, 400));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("icon.png")));
        setIconImage(icon.getImage());

        setVisible(true);
    }

    static void main() {
        SwingUtilities.invokeLater(BaseWindow::new);
    }
}
