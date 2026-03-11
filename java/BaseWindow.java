import javax.swing.*;

public class BaseWindow extends JFrame {

    public BaseWindow() {

        setTitle("Schach-Turnierverwaltung");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setContentPane(new BoardGui());

        setVisible(true);
    }

    static void main() {
        SwingUtilities.invokeLater(BaseWindow::new);
    }
}
