package gui.dialog;

import gui.DatabaseConnection;
import gui.dto.PlayerDto;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class NewTournamentDialog {

    static DefaultListModel<PlayerDto> leftModel;
    static DefaultListModel<PlayerDto> rightModel;

    public static void show() {
        JDialog dialog = new JDialog((Frame) null, "New Tournament", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JTextField name = new JTextField();
        JTextField city = new JTextField();
        JTextField base = new JTextField();
        JTextField move = new JTextField();

        name.setMaximumSize(new Dimension(250, 25));
        city.setMaximumSize(new Dimension(250, 25));
        base.setMaximumSize(new Dimension(250, 25));
        move.setMaximumSize(new Dimension(250, 25));

        name.setAlignmentX(Component.CENTER_ALIGNMENT);
        city.setAlignmentX(Component.CENTER_ALIGNMENT);
        base.setAlignmentX(Component.CENTER_ALIGNMENT);
        move.setAlignmentX(Component.CENTER_ALIGNMENT);

        ((AbstractDocument) base.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("\\d+")) super.insertString(fb, offset, string, attr);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("\\d*")) super.replace(fb, offset, length, text, attrs);
            }
        });

        ((AbstractDocument) move.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("\\d+")) super.insertString(fb, offset, string, attr);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("\\d*")) super.replace(fb, offset, length, text, attrs);
            }
        });

        JLabel nameLabel = new JLabel("Name");
        JLabel cityLabel = new JLabel("City");
        JLabel baseLabel = new JLabel("Base Time");
        JLabel moveLabel = new JLabel("Move Time");

        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        baseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        moveLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        form.add(nameLabel);
        form.add(name);
        form.add(Box.createVerticalStrut(5));

        form.add(cityLabel);
        form.add(city);
        form.add(Box.createVerticalStrut(5));

        form.add(baseLabel);
        form.add(base);
        form.add(Box.createVerticalStrut(5));

        form.add(moveLabel);
        form.add(move);
        form.add(Box.createVerticalStrut(10));

        main.add(form, BorderLayout.NORTH);

        List<PlayerDto> allPlayers = PlayerDto.getAsList("SELECT * FROM players");

        leftModel = new DefaultListModel<>();
        rightModel = new DefaultListModel<>();

        if (allPlayers != null) {
            for (PlayerDto p : allPlayers) leftModel.addElement(p);
        }

        JList<PlayerDto> leftList = new JList<>(leftModel);
        JList<PlayerDto> rightList = new JList<>(rightModel);

        leftList.setCellRenderer((list, value, index, isSelected, cellHasFocus) ->
                new DefaultListCellRenderer().getListCellRendererComponent(
                        list,
                        value.lastname + ", " + value.firstname,
                        index,
                        isSelected,
                        cellHasFocus
                )
        );

        rightList.setCellRenderer(leftList.getCellRenderer());

        JScrollPane leftScroll = new JScrollPane(leftList);
        JScrollPane rightScroll = new JScrollPane(rightList);

        rightScroll.setBorder(BorderFactory.createTitledBorder("Selected players"));

        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        JButton add = new JButton(">>");
        JButton remove = new JButton("<<");

        JButton ok = new JButton("OK");
        ok.setEnabled(false);

        Runnable validate = () -> {
            boolean textOk =
                    !name.getText().isBlank() &&
                            !city.getText().isBlank() &&
                            base.getText().matches("\\d+") &&
                            move.getText().matches("\\d+");

            boolean playersOk = rightModel.size() >= 2;

            ok.setEnabled(textOk && playersOk);
        };

        DocumentListener listener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { validate.run(); }
            public void removeUpdate(DocumentEvent e) { validate.run(); }
            public void changedUpdate(DocumentEvent e) { validate.run(); }
        };

        name.getDocument().addDocumentListener(listener);
        city.getDocument().addDocumentListener(listener);
        base.getDocument().addDocumentListener(listener);
        move.getDocument().addDocumentListener(listener);

        add.addActionListener(_ -> {
            for (PlayerDto p : leftList.getSelectedValuesList()) {
                leftModel.removeElement(p);
                rightModel.addElement(p);
            }
            validate.run();
        });

        remove.addActionListener(_ -> {
            for (PlayerDto p : rightList.getSelectedValuesList()) {
                rightModel.removeElement(p);
                leftModel.addElement(p);
            }
            validate.run();
        });

        ok.addActionListener(_ -> {
            addTournament(name.getText(), city.getText(), base.getText(), move.getText());
            dialog.dispose();
        });

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.45;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        center.add(leftScroll, c);

        JPanel btnPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        btnPanel.add(add);
        btnPanel.add(remove);

        c.gridx = 1;
        c.weightx = 0.1;
        c.fill = GridBagConstraints.NONE;
        center.add(btnPanel, c);

        c.gridx = 2;
        c.weightx = 0.45;
        c.fill = GridBagConstraints.BOTH;
        center.add(rightScroll, c);

        main.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.add(ok);

        dialog.setContentPane(main);
        dialog.add(bottom, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private static void addTournament(String name, String city, String base, String move) {
        try {
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

            String roundId = Objects.requireNonNull(DatabaseConnection.executeSql(
                    "SELECT id FROM rounds WHERE tournament_id = " + tournamentId + " AND round_number = 1;"
            )).getFirst().get("id");

            var players = DatabaseConnection.executeSql(
                    "SELECT p.id, p.fide_rating FROM players p " +
                            "JOIN player_tournament_info pti ON p.id = pti.player_id " +
                            "WHERE pti.tournament_id = " + tournamentId + " " +
                            "ORDER BY p.fide_rating DESC;"
            );

            assert players != null;
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

        } catch (Exception ignored) {
        }
    }
}