package Gui.TournamentGui;

import Gui.BaseWindow;
import Gui.BoardGui.BoardPanel;
import Gui.BoardGui.Game;
import Gui.DatabaseConnection;
import Gui.Dto.PlayerDto;
import Gui.Dto.RoundDto;
import Gui.Dto.TournamentDto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StartGamePanel extends JPanel {

    GridBagConstraints gridBagConstraints;

    public StartGamePanel(TournamentDto tournamentDto, RoundDto roundDto) {
        setLayout(new GridBagLayout());

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;

        JLabel tournamentLabel = new JLabel(tournamentDto.name + " | Round " + roundDto.round_number, SwingConstants.CENTER);
        tournamentLabel.setFont(tournamentLabel.getFont().deriveFont(Font.BOLD, 32f));

        JFormattedTextField boardNumber = new JFormattedTextField(NumberFormat.getIntegerInstance());

        List<HashMap<String, String>> playersHashMap = DatabaseConnection.executeSql("SELECT * FROM players;");
        List<PlayerDto> playerList = new ArrayList<>();
        if (playersHashMap != null && !playersHashMap.isEmpty()) {
            for (HashMap<String, String> player : playersHashMap) {
                try {
                    playerList.add(new PlayerDto(Integer.parseInt(player.get("id")), player.get("firstname"), player.get("lastname"), Integer.parseInt(player.get("fide_rating")), PlayerDto.fromKeyOrName(player.get("fide_title")) , player.get("gender").charAt(0), LocalDate.parse(player.get("birthdate")), PlayerDto.PlayerStatus.valueOf(player.get("status"))));
                } catch (Exception _) {}
            }
        }
        PlayerDto[] playerArray = playerList.toArray(new PlayerDto[0]);

        JComboBox<PlayerDto> whitePlayerCombobox = new JComboBox<>(playerArray);

        JComboBox<PlayerDto> blackPlayerCombobox = new JComboBox<>(playerArray);
        JButton cancelButton = new JButton("CANCEL");
        JButton startButton = new JButton("START");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlayerDto whitePlayer = (PlayerDto) whitePlayerCombobox.getSelectedItem();
                PlayerDto blackPlayer = (PlayerDto) blackPlayerCombobox.getSelectedItem();

                if (boardNumber.getText() == null || boardNumber.getText().isBlank() || whitePlayer == null || blackPlayer == null) {
                    JOptionPane.showMessageDialog(getInstance(), "Fehlende Angabe!");
                } else {
                    if (whitePlayer.id == blackPlayer.id) {
                        JOptionPane.showMessageDialog(getInstance(), "Es müssen zwei unterschiedliche Spieler ausgewählt werden!");
                    } else {
                        BaseWindow.getInstance().setContentPane(new BoardPanel(new Game(tournamentDto, roundDto, whitePlayer, blackPlayer)));
                    }
                }
            }
        });

        addComponent(0, 0, 2, tournamentLabel);
        addComponent(0, 1, 1, new JLabel("Board Number:", SwingConstants.CENTER));
        addComponent(1, 1, 1, boardNumber);
        addComponent(0, 2, 1, new JLabel("White Player:", SwingConstants.CENTER));
        addComponent(1, 2, 1, whitePlayerCombobox);
        addComponent(0, 3, 1, new JLabel("White Player:", SwingConstants.CENTER));
        addComponent(1, 3, 1, blackPlayerCombobox);
        addComponent(0, 4, 1, cancelButton);
        addComponent(1, 4, 1, startButton);
    }

    public void addComponent(int gridx, int gridy, int gridwidth, JComponent component) {
        gridBagConstraints.gridx = gridx;
        gridBagConstraints.gridy = gridy;
        gridBagConstraints.gridwidth = gridwidth;
        add(component, gridBagConstraints);
    }

    public StartGamePanel getInstance() {
        return this;
    }
}
