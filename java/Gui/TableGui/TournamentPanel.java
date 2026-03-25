package Gui.TableGui;

import Gui.Dto.TournamentDto;
import Gui.GuiTemplate;

import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TournamentPanel extends GuiTemplate<TournamentDto> {
    JComboBox<TournamentDto> tournamentSelection;
    public TournamentPanel() {
        super(TournamentDto.class, "tournament");
        List<TournamentDto> tournamentDtos = new ArrayList<>();
        tournamentDtos.add(new TournamentDto(1, "Tunir Test", LocalDate.now(), "Münster", 60, 30, TournamentDto.TournamentStatus.COMPLETED));

        tournamentSelection = new JComboBox<>(tournamentDtos.toArray(new TournamentDto[0]));

        add(tournamentSelection);
    }
}