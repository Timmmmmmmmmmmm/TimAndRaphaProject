package Gui.dialog;

import Gui.DatabaseConnection;
import Gui.dto.PlayerDto;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class NewTournamentDialog {

    static DefaultListModel<PlayerDto> leftModel;
    static DefaultListModel<PlayerDto> rightModel;

    public static void show() {
        JDialog dialog = new JDialog();
        dialog.setTitle("New Tournament");
        dialog.setSize(600, 450);
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout());
        JPanel main = new JPanel(new BorderLayout(10,10));
        main.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel form = new JPanel(new GridLayout(0,1,5,5));
        JTextField name = new JTextField();
        JTextField city = new JTextField();
        JTextField base = new JTextField();
        JTextField move = new JTextField();

        addField(form, "Name", name);
        addField(form, "City", city);
        addField(form, "Base Time", base);
        addField(form, "Move Time", move);
        main.add(form, BorderLayout.NORTH);

        List<PlayerDto> allPlayers = PlayerDto.getAsList("SELECT * FROM players");
        leftModel = new DefaultListModel<>();
        rightModel = new DefaultListModel<>();
        if (allPlayers != null) for (PlayerDto p : allPlayers) leftModel.addElement(p);

        JList<PlayerDto> leftList = new JList<>(leftModel);
        JList<PlayerDto> rightList = new JList<>(rightModel);
        leftList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            PlayerDto p = (PlayerDto)value;
            return new DefaultListCellRenderer().getListCellRendererComponent(list,
                    p.lastname + ", " + p.firstname, index, isSelected, cellHasFocus);
        });
        rightList.setCellRenderer(leftList.getCellRenderer());

        JScrollPane leftScroll = new JScrollPane(leftList);
        JScrollPane rightScroll = new JScrollPane(rightList);
        rightScroll.setBorder(BorderFactory.createTitledBorder("Selected players"));

        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        JButton add = new JButton(">>");
        JButton remove = new JButton("<<");

        add.addActionListener(e -> {
            for (PlayerDto p : leftList.getSelectedValuesList()) {
                leftModel.removeElement(p);
                rightModel.addElement(p);
            }
        });
        remove.addActionListener(e -> {
            for (PlayerDto p : rightList.getSelectedValuesList()) {
                rightModel.removeElement(p);
                leftModel.addElement(p);
            }
        });

        c.gridx = 0; c.gridy = 0; c.weightx = 0.45; c.weighty = 1; c.fill = GridBagConstraints.BOTH;
        center.add(leftScroll, c);

        JPanel btnPanel = new JPanel(new GridLayout(2,1,5,5));
        btnPanel.add(add);
        btnPanel.add(remove);

        c.gridx = 1; c.weightx = 0.1; c.fill = GridBagConstraints.NONE;
        center.add(btnPanel, c);

        c.gridx = 2; c.weightx = 0.45; c.fill = GridBagConstraints.BOTH;
        center.add(rightScroll, c);

        main.add(center, BorderLayout.CENTER);

        JButton ok = new JButton("OK");
        ok.addActionListener(_ -> {
            addTournament(name.getText(), city.getText(), base.getText(), move.getText());
            dialog.dispose();
        });

        dialog.add(main, BorderLayout.CENTER);
        dialog.add(ok, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private static void addTournament(String name, String city, String base, String move) {
        System.out.println("!!!NEUES TURNIER WIRD HINZUGEFÜGT!!!");
        String sql = "INSERT INTO tournaments (name, date, city, base_consider_time, move_consider_time, status) VALUES (" +
                "'" + name + "'," +
                "'" + LocalDate.now() + "'," +
                "'" + city + "'," +
                base + "," +
                move + ",'PLANNED');";

        String tournamentId = DatabaseConnection.insertAndReturnPrimaryKey(sql);

        for (int i = 0; i < rightModel.size(); i++) {
            DatabaseConnection.executeSql(
                    "INSERT INTO player_tournament_info (tournament_id, tournament_status, player_id, score) VALUES ('" +
                            tournamentId + "', 'APPLIED', '" + rightModel.get(i).id + "', 0);"
            );
        }

        int rounds = (int) Math.ceil(Math.log(rightModel.size()) / Math.log(2));

        for (int r = 1; r <= rounds; r++) {
            DatabaseConnection.executeSql(
                    "INSERT INTO rounds (tournament_id, round_number, status) VALUES (" +
                            tournamentId + ", " + r + ", 'PLANNED');"
            );
        }

        String roundId = DatabaseConnection.executeSql(
                "SELECT id FROM rounds WHERE tournament_id = " + tournamentId + " AND round_number = 1;"
        ).getFirst().get("id");

        var players = DatabaseConnection.executeSql(
                "SELECT p.id, p.fide_rating FROM players p " +
                        "JOIN player_tournament_info pti ON p.id = pti.player_id " +
                        "WHERE pti.tournament_id = " + tournamentId + " " +
                        "ORDER BY p.fide_rating DESC;"
        );

        int count = players.size();

        if (count % 2 != 0) {
            String byePlayer = players.get(count - 1).get("id");
            DatabaseConnection.executeSql(
                    "UPDATE player_tournament_info SET score = score + 1 WHERE player_id = " +
                            byePlayer + " AND tournament_id = " + tournamentId
            );
            count--;
        }

        int half = count / 2;

        for (int i = 0; i < half; i++) {
            String white = players.get(i).get("id");
            String black = players.get(i + half).get("id");

            if (i % 2 == 1) {
                String temp = white;
                white = black;
                black = temp;
            }

            DatabaseConnection.executeSql(
                    "INSERT INTO games (round_id, tournament_id, player_white, player_black, board_number) VALUES (" +
                            roundId + ", " +
                            tournamentId + ", " +
                            white + ", " +
                            black + ", " +
                            (i + 1) +
                            ");"
            );
        }
        System.out.println("!!!TURNIER WURDE HINZUGEFÜGT!!!");
    }

    private static void addField(JPanel panel, String name, JComponent comp) {
        panel.add(new JLabel(name));
        panel.add(comp);
    }
}
