package Gui.TournamentGui;

import Gui.BaseWindow;
import Gui.DatabaseConnection;
import Gui.Dto.PlayerDto;
import Gui.Dto.TournamentDto;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class StartTournamentPanel extends JPanel {

    private JTable table;
    private PlayerTableModel tableModel;

    public StartTournamentPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton newPlayerButton = new JButton("New Player");
        JButton newTournamentButton = new JButton("New Tournament");
        JButton openTournamentButton = new JButton("Open Tournament");

        topPanel.add(newPlayerButton);
        topPanel.add(newTournamentButton);
        topPanel.add(openTournamentButton);

        add(topPanel, BorderLayout.NORTH);

        tableModel = new PlayerTableModel();
        table = new JTable(tableModel);
        table.setRowHeight(28);

        JComboBox<PlayerDto.FideTitle> comboBox = new JComboBox<>(PlayerDto.FideTitle.values());
        table.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(comboBox));

        table.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(7).setCellEditor(new ButtonEditor());

        add(new JScrollPane(table), BorderLayout.CENTER);

        newPlayerButton.addActionListener(e -> openNewPlayerDialog());
        newTournamentButton.addActionListener(e -> openNewTournamentDialog());
        openTournamentButton.addActionListener(e -> openTournamentDialog());
    }

    private void openNewPlayerDialog() {
        JDialog dialog = createDialog("New Player", 400, 400);

        JPanel panel = createFormPanel();

        JTextField first = new JTextField();
        JTextField last = new JTextField();
        JTextField rating = new JTextField();
        JComboBox<PlayerDto.FideTitle> title = new JComboBox<>(PlayerDto.FideTitle.values());
        JTextField gender = new JTextField();
        JTextField birth = new JTextField();

        addField(panel, "Firstname", first);
        addField(panel, "Lastname", last);
        addField(panel, "Rating", rating);
        addField(panel, "Title", title);
        addField(panel, "Gender", gender);
        addField(panel, "Birthdate", birth);

        JButton ok = new JButton("OK");
        ok.addActionListener(e -> {
            String sql = "INSERT INTO players (firstname, lastname, fide_rating, fide_title, gender, birthdate) VALUES (" +
                    "'" + first.getText() + "'," +
                    "'" + last.getText() + "'," +
                    rating.getText() + "," +
                    "'" + title.getSelectedItem() + "'," +
                    "'" + gender.getText() + "'," +
                    "'" + birth.getText() + "')";
            DatabaseConnection.executeSql(sql);
            dialog.dispose();
            tableModel.reload();
        });

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(ok, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void openNewTournamentDialog() {
        JDialog dialog = createDialog("New Tournament", 600, 450);

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

        DefaultListModel<PlayerDto> leftModel = new DefaultListModel<>();
        DefaultListModel<PlayerDto> rightModel = new DefaultListModel<>();

        if (allPlayers != null) {
            for (PlayerDto p : allPlayers) leftModel.addElement(p);
        }

        JList<PlayerDto> leftList = new JList<>(leftModel);
        JList<PlayerDto> rightList = new JList<>(rightModel);

        JScrollPane leftScroll = new JScrollPane(leftList);
        JScrollPane rightScroll = new JScrollPane(rightList);
        rightScroll.setBorder(BorderFactory.createTitledBorder("Ausgewählte Spieler"));

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
        ok.addActionListener(e -> {
            String sql = "INSERT INTO tournaments (name, date, city, base_consider_time, move_consider_time, status) VALUES (" +
                    "'" + name.getText() + "'," +
                    "'" + LocalDate.now() + "'," +
                    "'" + city.getText() + "'," +
                    base.getText() + "," +
                    move.getText() + ",'PLANNED');";
            DatabaseConnection.executeSql(sql);

            String tournamentId = DatabaseConnection.executeSql("SELECT id FROM tournaments WHERE name = '" + name.getText() + "';").getFirst().get("id");
            for (int i = 0;i < rightModel.size();i++) {
                String playerId = DatabaseConnection.executeSql("SELECT id FROM players WHERE firstname = '" + rightModel.get(i).firstname + "' AND lastname = '" + rightModel.get(i).lastname + "';").getFirst().get("id");
                DatabaseConnection.executeSql("INSERT INTO player_tournament_info (tournament_id, tournament_status, player_id, score)" +
                        "VALUES ('" + tournamentId + "', 'APPLIED', '" + playerId + "', 0);");
            }
            dialog.dispose();
        });

        dialog.add(main, BorderLayout.CENTER);
        dialog.add(ok, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void openTournamentDialog() {
        JDialog dialog = createDialog("Open Tournament", 400, 200);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        List<TournamentDto> list = TournamentDto.getAsList("SELECT * FROM tournaments");
        JComboBox<TournamentDto> box = new JComboBox<>(list.toArray(new TournamentDto[0]));
        box.setPreferredSize(new Dimension(250, 30));

        JButton ok = new JButton("OK");
        ok.addActionListener(e -> {
            TournamentDto t = (TournamentDto) box.getSelectedItem();
            if (t != null) {
                BaseWindow.getInstance().setContentPane(new TournamentPanel(t));
                dialog.dispose();
            }
        });

        panel.add(box);
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(ok, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JDialog createDialog(String title, int w, int h) {
        JDialog dialog = new JDialog();
        dialog.setTitle(title);
        dialog.setSize(w, h);
        dialog.setLocationRelativeTo(this);
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout());
        return dialog;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(0,1,5,5));
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        return panel;
    }

    private void addField(JPanel panel, String name, JComponent comp) {
        panel.add(new JLabel(name));
        panel.add(comp);
    }

    class PlayerTableModel extends AbstractTableModel {

        private List<PlayerDto> players;
        private final String[] columns = {"ID","Firstname","Lastname","Rating","Title","Gender","Birthdate",""};

        public PlayerTableModel() {
            reload();
        }

        public void reload() {
            players = PlayerDto.getAsList("SELECT * FROM players");
            fireTableDataChanged();
        }

        public PlayerDto getPlayerAt(int row) {
            return players.get(row);
        }

        @Override public int getRowCount() { return players == null ? 0 : players.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int column) { return columns[column]; }

        @Override
        public boolean isCellEditable(int row, int col) {
            return col != 0;
        }

        @Override
        public Object getValueAt(int row, int col) {
            PlayerDto p = players.get(row);
            return switch (col) {
                case 0 -> p.id;
                case 1 -> p.firstname;
                case 2 -> p.lastname;
                case 3 -> p.fide_rating;
                case 4 -> p.fide_title;
                case 5 -> p.gender;
                case 6 -> p.birthdate;
                case 7 -> "Delete";
                default -> null;
            };
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            PlayerDto p = players.get(row);

            String sql = null;

            switch (col) {
                case 1 -> { p.firstname = value.toString(); sql = "firstname='" + p.firstname + "'"; }
                case 2 -> { p.lastname = value.toString(); sql = "lastname='" + p.lastname + "'"; }
                case 3 -> { p.fide_rating = Integer.parseInt(value.toString()); sql = "fide_rating=" + p.fide_rating; }
                case 4 -> { p.fide_title = (PlayerDto.FideTitle) value; sql = "fide_title='" + p.fide_title.getKey() + "'"; }
                case 5 -> { p.gender = value.toString().charAt(0); sql = "gender='" + p.gender + "'"; }
                case 6 -> { p.birthdate = PlayerDto.parseLocalDate(value.toString()); sql = "birthdate='" + p.birthdate + "'"; }
            }

            if (sql != null) {
                DatabaseConnection.executeSql("UPDATE players SET " + sql + " WHERE id=" + p.id);
            }

            fireTableRowsUpdated(row, row);
        }
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() { setText("Delete"); }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class ButtonEditor extends AbstractCellEditor implements TableCellEditor {

        private final JButton button = new JButton("Delete");
        private int row;

        public ButtonEditor() {
            button.addActionListener(e -> {
                int r = row;
                fireEditingStopped();

                SwingUtilities.invokeLater(() -> {
                    PlayerDto p = tableModel.getPlayerAt(r);

                    DatabaseConnection.executeSql("DELETE FROM players WHERE id=" + p.id);
                    tableModel.reload();
                });
            });
        }

        @Override
        public Object getCellEditorValue() {
            return "Delete";
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            return button;
        }
    }
}