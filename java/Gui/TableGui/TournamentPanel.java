package Gui.TableGui;

import Gui.Dto.Tournament;
import Gui.GuiTemplate;

import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TournamentPanel extends GuiTemplate<Tournament> {
    JComboBox<Tournament> tournamentSelection;
    public TournamentPanel() {
        super(Tournament.class, "tournament");
        List<Tournament> tournaments = new ArrayList<>();
        tournaments.add(new Tournament(1, "Tunir Test", LocalDate.now(), "Münster", 60, 30, Tournament.TournamentStatus.FINISHED));

        tournamentSelection = new JComboBox<>(tournaments.toArray(new Tournament[0]));

        add(tournamentSelection);
    }
}