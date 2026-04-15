package gui.panel;

import gui.BaseWindow;
import gui.DatabaseConnection;
import gui.dto.PlayerTournamentInfoDto;
import gui.dto.TournamentDto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TournamentResultPanel extends JPanel {

    private final TournamentDto tournamentDto;

    private JTable playerTable;
    private DefaultTableModel tableModel;

    private boolean showDetails = false;
    private JPanel playerDetailsPanel;
    private JTable playerDetailsTable;
    private DefaultTableModel detailsModel;

    private final List<PlayerTournamentInfoDto> players;

    public TournamentResultPanel(TournamentDto tournamentDto) {
        this.tournamentDto = tournamentDto;

        players = PlayerTournamentInfoDto.getAsList(
                "SELECT * FROM players p INNER JOIN player_tournament_info pti ON p.id = pti.player_id WHERE pti.tournament_id = "
                        + tournamentDto.id() + " ORDER BY score DESC");

        setLayout(new BorderLayout(10, 10));

        add(createScrollContent(), BorderLayout.CENTER);
    }

    private JScrollPane createScrollContent() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(10, 10, 10, 10));

        content.add(createMenuPanel());
        content.add(Box.createVerticalStrut(10));
        content.add(createWinnerPanel());
        content.add(Box.createVerticalStrut(10));
        content.add(createPlayerTablePanel());
        content.add(Box.createVerticalStrut(10));
        content.add(createPlayerDetailsPanel());
        content.add(Box.createVerticalStrut(10));
        content.add(createTournamentInfoPanel());

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        return scrollPane;
    }

    public JPanel createMenuPanel() {
        JButton backButton = new JButton("Exit tournament");
        backButton.addActionListener(_ -> {
            BaseWindow.getInstance().setContentPane(new StartTournamentPanel());
            BaseWindow.getInstance().revalidate();
            BaseWindow.getInstance().repaint();
        });

        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        menuPanel.add(backButton);
        menuPanel.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, menuPanel.getPreferredSize().height)
        );
        return menuPanel;
    }

    private JPanel createWinnerPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel winnerLabel = new JLabel("Winner: " + players.getFirst().toString());
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/gui/assets/pieces/bK.png")));
        winnerLabel.setIcon(icon);
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 20));

        panel.add(winnerLabel, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));

        return panel;
    }

    private JPanel createPlayerTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Players"));

        String[] columns = {"#", "Score", "Name", "FIDE"};

        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        playerTable = new JTable(tableModel);
        playerTable.setRowHeight(22);

        loadPlayers();

        playerTable.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                int row = playerTable.getSelectedRow();
                if (row >= 0) {
                    showDetails = false;
                    showPlayerDetails(row);
                }
            }
        });

        int height = playerTable.getRowHeight() * players.size() + playerTable.getTableHeader().getPreferredSize().height;

        panel.add(playerTable, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));

        return panel;
    }

    private void loadPlayers() {
        for (int i = 0; i < players.size(); i++) {
            PlayerTournamentInfoDto player = players.get(i);

            tableModel.addRow(new Object[]{
                    i + 1,
                    player.score,
                    player.player.firstname + " " + player.player.lastname,
                    player.player.fide_rating + " (" + player.player.fide_title.getKey() + ")"
            });
        }
    }

    private JPanel createPlayerDetailsPanel() {
        playerDetailsPanel = new JPanel(new BorderLayout());
        playerDetailsPanel.setBorder(BorderFactory.createTitledBorder("Player details"));

        detailsModel = new DefaultTableModel(new String[]{"Attribute", "Value"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        playerDetailsTable = new JTable(detailsModel);
        playerDetailsTable.setRowHeight(22);

        playerDetailsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = playerDetailsTable.rowAtPoint(evt.getPoint());
                if (row == 6) {
                    showDetails = !showDetails;
                    showPlayerDetails(playerTable.getSelectedRow());
                }
            }
        });

        detailsModel.addRow(new Object[]{"", ""});
        int height = detailsModel.getRowCount() * playerDetailsTable.getRowHeight();

        playerDetailsTable.setPreferredSize(new Dimension(0, height));
        playerDetailsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, height + 30));

        playerDetailsPanel.add(playerDetailsTable, BorderLayout.CENTER);
        return playerDetailsPanel;
    }

    private void showPlayerDetails(int index) {
        detailsModel.setRowCount(0);

        PlayerTournamentInfoDto player = players.get(index);
        int playerId = player.player.id;

        String games_played;
        String wins;
        String losses;
        String draws;

        try {
            games_played = Objects.requireNonNull(DatabaseConnection.executeSql("SELECT COUNT(*) AS count FROM games g WHERE g.tournament_id = " + tournamentDto.id() + " AND (g.player_white = " + playerId + " OR g.player_black = " + playerId + ");")).getFirst().get("count");
            wins = Objects.requireNonNull(DatabaseConnection.executeSql("SELECT COUNT(*) AS count FROM games g WHERE g.tournament_id = " + tournamentDto.id() + " AND ((g.player_white = " + playerId + " AND g.result = 1) OR (g.player_black = " + playerId + " AND g.result = -1));")).getFirst().get("count");
            losses = Objects.requireNonNull(DatabaseConnection.executeSql("SELECT COUNT(*) AS count FROM games g WHERE g.tournament_id = " + tournamentDto.id() + " AND ((g.player_white = " + playerId + " AND g.result = -1) OR (g.player_black = " + playerId + " AND g.result = 1));")).getFirst().get("count");
            draws = Objects.requireNonNull(DatabaseConnection.executeSql("SELECT COUNT(*) AS count FROM games g WHERE g.tournament_id = " + tournamentDto.id() + " AND g.result = 0 AND (g.player_white = " + playerId + " OR g.player_black = " + playerId + ");")).getFirst().get("count");

        } catch (Exception e) {
            System.out.println("Error while trying to load details for player with id " + playerId);
            return;
        }

        List<HashMap<String, String>> strongestList = DatabaseConnection.executeSql(
                "SELECT p.firstname, p.lastname, p.fide_rating FROM games g JOIN players p ON ((g.player_white = "
                        + playerId + " AND p.id = g.player_black) OR (g.player_black = "
                        + playerId + " AND p.id = g.player_white)) WHERE g.tournament_id = "
                        + tournamentDto.id() + " AND p.fide_rating IS NOT NULL ORDER BY p.fide_rating DESC LIMIT 1;"
        );

        double winPercent = 0;
        if (!games_played.equals("0")) {
            winPercent = (Double.parseDouble(wins) / Double.parseDouble(games_played)) * 100;
        }

        String strongest = "-";
        if (strongestList != null && !strongestList.isEmpty()) {
            HashMap<String, String> m = strongestList.getFirst();
            System.out.println(m);
            strongest = m.get("lastname") + ", " + m.get("firstname") + " (" + m.get("fide_rating") + ")";
        }

        detailsModel.addRow(new Object[]{"Firstname", player.player.firstname});
        detailsModel.addRow(new Object[]{"Lastname", player.player.lastname});
        detailsModel.addRow(new Object[]{"Birthdate", player.player.birthdate});
        detailsModel.addRow(new Object[]{"Gender", player.player.gender});
        detailsModel.addRow(new Object[]{"FIDE Rating", player.player.fide_rating + " (" + player.player.fide_title.getKey() + ")"});
        detailsModel.addRow(new Object[]{"Score", player.score});
        detailsModel.addRow(new Object[]{showDetails ? "Games played ▼" : "Games played ▶", games_played});

        if (showDetails) {
            detailsModel.addRow(new Object[]{"Wins", wins});
            detailsModel.addRow(new Object[]{"Losses", losses});
            detailsModel.addRow(new Object[]{"Draws", draws});
        }

        detailsModel.addRow(new Object[]{"Win %", String.format("%.1f%%", winPercent)});
        detailsModel.addRow(new Object[]{"Strongest opponent", strongest});

        int height = detailsModel.getRowCount() * playerDetailsTable.getRowHeight();

        playerDetailsTable.setPreferredSize(new Dimension(0, height));
        playerDetailsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, height + 30));

        playerDetailsPanel.revalidate();
        playerDetailsPanel.repaint();
    }

    private JPanel createTournamentInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Tournament Info"));

        DefaultTableModel model = new DefaultTableModel(new String[]{"Attribute", "Value"}, 0){
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(22);
        table.setTableHeader(null);

        model.addRow(new Object[]{"Name", tournamentDto.name()});
        model.addRow(new Object[]{"City", tournamentDto.city()});
        model.addRow(new Object[]{"Date", tournamentDto.date()});

        int height = table.getRowHeight() * model.getRowCount();

        table.setPreferredSize(new Dimension(0, height));

        panel.add(table, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, height + 20));

        return panel;
    }
}