import BoardGui.BoardPanel;
import BoardGui.Game;

import javax.swing.*;

public class BaseWindow extends JFrame {

    public BaseWindow() {

        Game game = new Game();

        setTitle("Schach-Turnierverwaltung");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setContentPane(new BoardPanel(game));

        setVisible(true);
    }

    static void main() {
        SwingUtilities.invokeLater(BaseWindow::new);
    }
}
