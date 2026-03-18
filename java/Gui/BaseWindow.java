package Gui;

import Gui.BoardGui.BoardPanel;
import Gui.BoardGui.Game;
import Gui.Dto.Player;
import Gui.Dto.Round;
import Gui.Dto.Tournament;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Objects;

public class BaseWindow extends JFrame {

    private static BaseWindow instance;

    public BaseWindow() {
        instance = this;

        setTitle("Schach-Turnierverwaltung");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setContentPane(new BoardPanel(new Game(new Tournament(1, "Münsterland-Tunier", LocalDate.now(), "Münster", 90, 30, Tournament.TournamentStatus.RUNNING),
                new Round(1, 1, Round.RoundStatus.RUNNING, LocalDateTime.now()),
                new Player(1, "Raphael", "Berkenheide", 15, Player.FideTitle.NONE, 'm', LocalDate.of(2008, Month.MAY, 31), Player.PlayerStatus.PLAYING),
                new Player(2, "Tim", "Kaiser", 1500, Player.FideTitle.GRANDMASTER, 'm', LocalDate.of(2003, Month.MAY, 13), Player.PlayerStatus.PLAYING))));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setMinimumSize(new Dimension(screenSize.width / 4, screenSize.height / 4));
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
