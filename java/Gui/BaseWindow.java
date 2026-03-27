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
//        setContentPane(new BoardPanel(new Game(new TournamentDto(1, "Münsterland-Tunier", LocalDate.now(), "Münster", 90, 30, TournamentDto.TournamentStatus.ACTIVE),
//                new RoundDto(1, 1, RoundDto.RoundStatus.RUNNING, LocalDateTime.now()),
//                new GameDto(1, 0, LocalDateTime.now(), 1, 1, 1, 2, 1),
//                new PlayerDto(1, "Raphael", "Berkenheide", 15, PlayerDto.FideTitle.NONE, 'm', LocalDate.of(2008, Month.MAY, 31)),
//                new PlayerDto(2, "Tim", "Kaiser", 1500, PlayerDto.FideTitle.GRANDMASTER, 'm', LocalDate.of(2003, Month.MAY, 13)))));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setMinimumSize(new Dimension(screenSize.width / 4, screenSize.height / 4));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);

        //setContentPane(new StartGamePanel(new TournamentDto(1, "Münsterland-Tunier", LocalDate.now(), "Münster", 90, 30, TournamentDto.TournamentStatus.ACTIVE), new RoundDto(1, 1, RoundDto.RoundStatus.PLANNED, LocalDateTime.now())));

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Tournaments", new GuiTemplate<>(TournamentDto.class, "tournaments"));
        tabs.add("Players", new GuiTemplate<>(PlayerDto.class, "players"));
        tabs.add("Rounds", new GuiTemplate<>(RoundDto.class, "rounds"));
        //setContentPane(tabs);

        setContentPane(new TournamentPanel(new TournamentDto(1, "Münsterland-Tunier", LocalDate.now(), "Münster", 90, 30, TournamentDto.TournamentStatus.ACTIVE)));

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("icon.png")));
        setIconImage(icon.getImage());

        setVisible(true);
        revalidate();
        repaint();

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
