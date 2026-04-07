package Gui.TournamentGui;

import Gui.BaseWindow;
import Gui.Dto.*;
import Gui.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public class TournamentPanel extends JPanel {

    Integer currentRound = null;
    boolean roundActive = false;

    private ImageIcon tournamentIcon;
    private ImageIcon roundPlannedIcon;
    private ImageIcon roundRunningIcon;
    private ImageIcon roundCompletedIcon;
    private ImageIcon gameOpenIcon;
    private ImageIcon gameClosedIcon;

    private JTree tree;
    private DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeModel;

    private JButton startGameButton;
    private JButton backButton;
    private JButton nextRoundButton;
    private JTable leaderboardTable;

    private GameRoundPlayerDto selectedGame;
    private TournamentDto tournamentDto;

    public TournamentPanel(TournamentDto tournamentDto) {
        this.tournamentDto = tournamentDto;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        rootNode = new DefaultMutableTreeNode(tournamentDto.name);
        treeModel = new DefaultTreeModel(rootNode);
        tree = new JTree(treeModel);

        nextRoundButton = new JButton();

        JScrollPane treeScrollPane = new JScrollPane(tree);

        DefaultTableModel tableModel = new DefaultTableModel(
                new Object[]{"Position", "Name", "Score"}, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        leaderboardTable = new JTable(tableModel);
        leaderboardTable.setFillsViewportHeight(true);
        JScrollPane leaderboardScrollPane = new JScrollPane(leaderboardTable);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScrollPane, leaderboardScrollPane);
        splitPane.setResizeWeight(0.5);

        startGameButton = new JButton("Start selected game");
        backButton = new JButton("Exit tournament");

        startGameButton.addActionListener(e -> {
            if (selectedGame == null) return;

            if (selectedGame.gameDto.result != null) {
                JOptionPane.showMessageDialog(this, "Game is already completed!");
                return;
            }

            if (!roundActive || currentRound != selectedGame.roundDto.round_number) {
                JOptionPane.showMessageDialog(this, "This round is not active!");
                return;
            }

            StartGameDialog.showStart(tournamentDto, selectedGame);
        });

        backButton.addActionListener(e -> {
            BaseWindow.getInstance().setContentPane(new StartTournamentPanel());
            BaseWindow.getInstance().revalidate();
        });

        nextRoundButton.addActionListener(_ -> handleRoundButton());

        JPanel bottomPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        bottomPanel.add(backButton);
        bottomPanel.add(startGameButton);
        bottomPanel.add(nextRoundButton);

        add(splitPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node == null) return;
            selectedGame = node.getUserObject() instanceof GameRoundPlayerDto g ? g : null;
        });

        loadLeaderboard(tableModel);

        SwingUtilities.invokeLater(() -> setupUI());

        refreshTree();
    }

    private void setupUI() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double scale = Math.max(screenSize.width / 1920.0, screenSize.height / 1080.0);

        int rowHeight = (int) (24 * scale);
        tree.setRowHeight(rowHeight);

        tournamentIcon = scaleIcon("/Gui/assets/analysis/theory.png", rowHeight);
        roundPlannedIcon = scaleIcon("/Gui/assets/analysis/inaccuracy.png", rowHeight);
        roundRunningIcon = scaleIcon("/Gui/assets/analysis/mistake.png", rowHeight);
        roundCompletedIcon = scaleIcon("/Gui/assets/analysis/blunder.png", rowHeight);
        gameOpenIcon = scaleIcon("/Gui/assets/analysis/critical.png", rowHeight);
        gameClosedIcon = scaleIcon("/Gui/assets/analysis/brilliant.png", rowHeight);

        tree.setCellRenderer(new DefaultTreeCellRenderer() {
            public Component getTreeCellRendererComponent(JTree t, Object v, boolean sel, boolean exp, boolean leaf, int row, boolean focus) {
                super.getTreeCellRendererComponent(t, v, sel, exp, leaf, row, focus);

                DefaultMutableTreeNode node = (DefaultMutableTreeNode) v;
                Object o = node.getUserObject();

                if (o instanceof RoundDto r) {
                    setText(r.toString());
                    switch (r.status) {
                        case PLANNED -> setIcon(roundPlannedIcon);
                        case RUNNING -> setIcon(roundRunningIcon);
                        default -> setIcon(roundCompletedIcon);
                    }
                } else if (o instanceof GameRoundPlayerDto g) {
                    setText(g.toString());
                    setIcon(g.gameDto.result == null ? gameOpenIcon : gameClosedIcon);
                } else {
                    setIcon(tournamentIcon);
                }

                return this;
            }
        });

        float fontSize = (float) (14 * scale);
        tree.setFont(tree.getFont().deriveFont(fontSize));

        startGameButton.setFont(startGameButton.getFont().deriveFont(fontSize));
        backButton.setFont(backButton.getFont().deriveFont(fontSize));

        leaderboardTable.setFont(leaderboardTable.getFont().deriveFont(fontSize * 0.9f));
        leaderboardTable.setRowHeight((int) (fontSize * 2.2));
    }

    private ImageIcon scaleIcon(String path, int size) {
        return new ImageIcon(
                new ImageIcon(getClass().getResource(path))
                        .getImage()
                        .getScaledInstance(size, size, Image.SCALE_SMOOTH)
        );
    }

    private void handleRoundButton() {
        if (roundActive) {
            if (!isRoundFinished(currentRound)) {
                JOptionPane.showMessageDialog(this, "Not all games in this round have been completed!");
                return;
            }

            DatabaseConnection.executeSql(
                    "UPDATE rounds SET status = 'COMPLETED' WHERE round_number = " + currentRound +
                            " AND tournament_id = " + tournamentDto.id
            );

            if (isLastRound(currentRound)) {
                finishTournament();
            } else {
                currentRound++;
                roundActive = false;
            }
        } else {
            var games = DatabaseConnection.executeSql(
                    "SELECT * FROM games WHERE round_id = (SELECT id FROM rounds WHERE round_number = " +
                            currentRound + " AND tournament_id = " + tournamentDto.id + ")"
            );

            if (games == null || games.isEmpty()) {
                generatePairings(currentRound);
            }

            DatabaseConnection.executeSql(
                    "UPDATE rounds SET status = 'RUNNING', begin = '" + LocalDateTime.now() + "' " +
                            "WHERE round_number = " + currentRound +
                            " AND tournament_id = " + tournamentDto.id
            );

            roundActive = true;
        }

        updateButtonText();
        refreshTree();
        loadLeaderboard((DefaultTableModel) leaderboardTable.getModel());
    }

    private void updateButtonText() {
        nextRoundButton.setText(
                roundActive ? "Complete round " + currentRound : "Start round " + currentRound
        );
    }

    private void refreshTree() {
        rootNode = new DefaultMutableTreeNode(tournamentDto.name);

        List<RoundDto> rounds = RoundDto.getAsList(
                "SELECT * FROM rounds WHERE tournament_id = " + tournamentDto.id + " ORDER BY round_number;"
        );

        if (rounds != null) {
            currentRound = null;
            roundActive = false;

            for (RoundDto round : rounds) {
                if (currentRound == null && round.status != RoundDto.RoundStatus.COMPLETED) {
                    currentRound = round.round_number;
                    roundActive = round.status == RoundDto.RoundStatus.RUNNING;
                }

                DefaultMutableTreeNode roundNode = new DefaultMutableTreeNode(round);

                List<GameDto> games = GameDto.getAsList(
                        "SELECT * FROM games WHERE round_id = " + round.id
                );

                if (games != null) {
                    for (GameDto g : games) {
                        PlayerDto w = PlayerDto.getAsList("SELECT * FROM players WHERE id = " + g.player_white).getFirst();
                        PlayerDto b = PlayerDto.getAsList("SELECT * FROM players WHERE id = " + g.player_black).getFirst();

                        roundNode.add(new DefaultMutableTreeNode(
                                new GameRoundPlayerDto(g, round, w, b)
                        ));
                    }
                }

                rootNode.add(roundNode);
            }
        }

        treeModel.setRoot(rootNode);
        treeModel.reload();

        if (currentRound != null) {
            tree.expandRow(currentRound);
        }

        updateButtonText();
    }

    private void loadLeaderboard(DefaultTableModel model) {
        model.setRowCount(0);

        var result = DatabaseConnection.executeSql(
                "SELECT concat(firstname, ' ', lastname) AS name, i.score FROM players p " +
                        "INNER JOIN player_tournament_info i ON p.id = i.player_id " +
                        "WHERE tournament_id = '" + tournamentDto.id + "' " +
                        "ORDER BY i.score DESC LIMIT 10"
        );

        int rank = 1;

        if (result != null) {
            for (var row : result) {
                model.addRow(new Object[]{
                        rank++,
                        row.get("name"),
                        row.get("score")
                });
            }
        }
    }

    private void generatePairings(int roundNumber) {
        String roundId = DatabaseConnection.executeSql(
                "SELECT id FROM rounds WHERE tournament_id = " + tournamentDto.id +
                        " AND round_number = " + roundNumber
        ).getFirst().get("id");

        DatabaseConnection.executeSql("DELETE FROM games WHERE round_id = " + roundId);

        var players = DatabaseConnection.executeSql(
                "SELECT p.id, pti.score, p.fide_rating FROM players p " +
                        "JOIN player_tournament_info pti ON p.id = pti.player_id " +
                        "WHERE pti.tournament_id = " + tournamentDto.id +
                        " ORDER BY pti.score DESC, p.fide_rating DESC"
        );

        int count = players.size();

        if (count % 2 != 0) {
            String bye = players.get(count - 1).get("id");
            DatabaseConnection.executeSql(
                    "UPDATE player_tournament_info SET score = score + 1 WHERE player_id = " +
                            bye + " AND tournament_id = " + tournamentDto.id
            );
            count--;
        }

        int board = 1;

        for (int i = 0; i < count; i += 2) {
            String p1 = players.get(i).get("id");
            String p2 = players.get(i + 1).get("id");

            String white = (i / 2 % 2 == 0) ? p1 : p2;
            String black = white.equals(p1) ? p2 : p1;

            DatabaseConnection.executeSql(
                    "INSERT INTO games (round_id, tournament_id, player_white, player_black, board_number) VALUES (" +
                            roundId + "," + tournamentDto.id + "," + white + "," + black + "," + board++ + ")"
            );
        }
    }

    private boolean isRoundFinished(int roundNumber) {
        var result = DatabaseConnection.executeSql(
                "SELECT COUNT(*) as c FROM games g JOIN rounds r ON g.round_id = r.id " +
                        "WHERE r.tournament_id = " + tournamentDto.id +
                        " AND r.round_number = " + roundNumber +
                        " AND g.result IS NULL"
        );

        return result.getFirst().get("c").equals("0");
    }

    private boolean isLastRound(int roundNumber) {
        var result = DatabaseConnection.executeSql(
                "SELECT COUNT(*) as c FROM rounds WHERE tournament_id = " + tournamentDto.id
        );

        return roundNumber >= Integer.parseInt(result.getFirst().get("c"));
    }

    private void finishTournament() {
        DatabaseConnection.executeSql(
                "UPDATE tournaments SET status = 'COMPLETED' WHERE id = " + tournamentDto.id
        );
        TournamentResultDialog.showResult();
    }
}