package Gui.TournamentGui;

import Gui.DatabaseConnection;
import Gui.Dto.TournamentDto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StartTournamentPanel extends JPanel {
    JButton newButton;
    JButton openButton;

    public StartTournamentPanel() {

        newButton = new JButton("New Tournament");
        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dialog = new JDialog();
                dialog.setTitle("Create Tournament");
                dialog.setSize(400, 350);
                dialog.setLocationRelativeTo(null);
                dialog.setModal(true);
                dialog.setLayout(new BorderLayout());

                JPanel mainPanel = new JPanel();
                mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

                JTextField nameField = new JTextField();
                JTextField cityField = new JTextField();
                JTextField baseTimeField = new JTextField();
                JTextField moveTimeField = new JTextField();

                Dimension fieldSize = new Dimension(250, 30);

                java.util.function.Function<String, JLabel> createLabel = text -> {
                    JLabel label = new JLabel(text);
                    label.setAlignmentX(Component.CENTER_ALIGNMENT);
                    return label;
                };

                for (JTextField field : new JTextField[]{nameField, cityField, baseTimeField, moveTimeField}) {
                    field.setMaximumSize(fieldSize);
                    field.setAlignmentX(Component.CENTER_ALIGNMENT);
                }

                JButton okButton = new JButton("OK");
                okButton.setAlignmentX(Component.CENTER_ALIGNMENT);

                okButton.addActionListener(_ -> {
                    String name = nameField.getText();
                    String city = cityField.getText();
                    int baseTime = Integer.parseInt(baseTimeField.getText());
                    int moveTime = Integer.parseInt(moveTimeField.getText());

                    // TODO: Hier Datenbank INSERT für Turnier einbauen

                    dialog.dispose();
                });

                mainPanel.add(Box.createVerticalGlue());

                mainPanel.add(createLabel.apply("Name"));
                mainPanel.add(Box.createVerticalStrut(5));
                mainPanel.add(nameField);
                mainPanel.add(Box.createVerticalStrut(10));

                mainPanel.add(createLabel.apply("City"));
                mainPanel.add(Box.createVerticalStrut(5));
                mainPanel.add(cityField);
                mainPanel.add(Box.createVerticalStrut(10));

                mainPanel.add(createLabel.apply("Base Consider Time"));
                mainPanel.add(Box.createVerticalStrut(5));
                mainPanel.add(baseTimeField);
                mainPanel.add(Box.createVerticalStrut(10));

                mainPanel.add(createLabel.apply("Move Consider Time"));
                mainPanel.add(Box.createVerticalStrut(5));
                mainPanel.add(moveTimeField);
                mainPanel.add(Box.createVerticalStrut(20));

                mainPanel.add(okButton);

                mainPanel.add(Box.createVerticalGlue());

                dialog.add(mainPanel, BorderLayout.CENTER);

                dialog.setVisible(true);
            }
        });
        add(newButton);

        openButton = new JButton("Open Tournament");
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dialog = new JDialog();
                dialog.setTitle("Open Tournament");
                dialog.setSize(400, 300);
                dialog.setLocationRelativeTo(null);
                dialog.setModal(true);
                dialog.setLayout(new BorderLayout());

                JPanel mainPanel = new JPanel();
                mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

                List<HashMap<String, String>> tournamentsHashMap = DatabaseConnection.executeSql("SELECT * FROM tournaments;");

                List<TournamentDto> tournamentList = new ArrayList<>();

                if (tournamentsHashMap != null && !tournamentsHashMap.isEmpty()) {
                    for (HashMap<String, String> tournament : tournamentsHashMap) {
                        try {
                            tournamentList.add(new TournamentDto(
                                    Integer.parseInt(tournament.get("id")),
                                    tournament.get("name"),
                                    LocalDate.parse(tournament.get("date")),
                                    tournament.get("city"),
                                    Integer.parseInt(tournament.get("base_consider_time")),
                                    Integer.parseInt(tournament.get("move_consider_time")),
                                    TournamentDto.TournamentStatus.valueOf(tournament.get("status"))
                            ));
                        } catch (Exception ignored) {}
                    }
                }

                TournamentDto[] tournamentArray = tournamentList.toArray(new TournamentDto[0]);

                JComboBox<TournamentDto> tournamentComboBox = new JComboBox<>(tournamentArray);
                tournamentComboBox.setMaximumSize(new Dimension(250, 30));
                tournamentComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);

                JButton okButton = new JButton("OK");
                okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                okButton.addActionListener(_ -> dialog.dispose());

                mainPanel.add(Box.createVerticalGlue());
                mainPanel.add(tournamentComboBox);
                mainPanel.add(Box.createVerticalStrut(20));
                mainPanel.add(okButton);
                mainPanel.add(Box.createVerticalGlue());

                dialog.add(mainPanel, BorderLayout.CENTER);

                dialog.setVisible(true);
            }
        });
        add(openButton);
    }

    public StartTournamentPanel getInstance() {
        return this;
    }
}
