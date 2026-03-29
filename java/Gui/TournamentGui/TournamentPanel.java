package Gui.TournamentGui;

import Gui.BaseWindow;
import Gui.BoardGui.BoardPanel;
import Gui.BoardGui.Game;
import Gui.Dto.GameDto;
import Gui.Dto.PlayerDto;
import Gui.Dto.RoundDto;
import Gui.Dto.TournamentDto;
import Gui.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.HashMap;
import java.util.List;

public class TournamentPanel extends JPanel {

    private DefaultTreeCellRenderer renderer;
    private ImageIcon folderIcon;
    private ImageIcon openFolderIcon;
    private ImageIcon fileIcon;
    private JTree tree;
    private JButton startGameButton;
    private JButton backButton;
    private JTable leaderboardTable;
    private GameRoundPlayerDto selectedGame;
    private TournamentDto tournamentDto;

    public TournamentPanel(TournamentDto tournamentDto) {
        this.tournamentDto = tournamentDto;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultMutableTreeNode tournamentNode = new DefaultMutableTreeNode(tournamentDto.name);

        List<RoundDto> rounds = RoundDto.getAsList(
                "SELECT * FROM rounds WHERE tournament_id = " + tournamentDto.id + " ORDER BY round_number;"
        );

        if (rounds != null) {
            for (RoundDto roundDto : rounds) {
                DefaultMutableTreeNode roundNode = new DefaultMutableTreeNode("Round " + roundDto.round_number);

                List<GameDto> games = GameDto.getAsList(
                        "SELECT * FROM games WHERE round_id = " + roundDto.id + ";"
                );

                if (games != null) {
                    for (GameDto gameDto : games) {
                        PlayerDto whitePlayer = PlayerDto.getAsList("SELECT * FROM players WHERE id = " + gameDto.player_white + ";").getFirst();
                        PlayerDto blackPlayer = PlayerDto.getAsList("SELECT * FROM players WHERE id = " + gameDto.player_black + ";").getFirst();
                        GameRoundPlayerDto game = new GameRoundPlayerDto(gameDto, roundDto, whitePlayer, blackPlayer);
                        DefaultMutableTreeNode gameNode = new DefaultMutableTreeNode(game);
                        roundNode.add(gameNode);
                    }
                }

                tournamentNode.add(roundNode);
            }
        }

        renderer = new DefaultTreeCellRenderer();
        folderIcon = new ImageIcon(getClass().getResource("/Gui/assets/images/treeIcons/folderIcon.png"));
        openFolderIcon = new ImageIcon(getClass().getResource("/Gui/assets/images/treeIcons/openFolderIcon.png"));
        fileIcon = new ImageIcon(getClass().getResource("/Gui/assets/images/treeIcons/fileIcon.png"));

        renderer.setClosedIcon(folderIcon);
        renderer.setOpenIcon(openFolderIcon);
        renderer.setLeafIcon(fileIcon);

        tree = new JTree(tournamentNode);
        tree.setCellRenderer(renderer);

        JScrollPane treeScrollPane = new JScrollPane(tree);

        DefaultTableModel tableModel = new DefaultTableModel(
                new Object[]{"Platz", "Name", "Punkte"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        leaderboardTable = new JTable(tableModel);
        leaderboardTable.setFillsViewportHeight(true);
        JScrollPane leaderboardScrollPane = new JScrollPane(leaderboardTable);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScrollPane, leaderboardScrollPane);
        splitPane.setResizeWeight(0.5);

        startGameButton = new JButton("Game starten");
        backButton = new JButton("Zurück");

        startGameButton.addActionListener(e -> {
            if (selectedGame != null && selectedGame.gameDto.result == null) {
                BaseWindow.getInstance().setContentPane(
                        new BoardPanel(new Game(
                                tournamentDto,
                                selectedGame.roundDto,
                                selectedGame.gameDto,
                                selectedGame.whitePlayer,
                                selectedGame.blackPlayer
                        ))
                );
                BaseWindow.getInstance().revalidate();
            } else if (selectedGame != null) {
                JOptionPane.showMessageDialog(this, "Spiel wurde bereits beendet!");
            }
        });

        backButton.addActionListener(e -> {
            BaseWindow.getInstance().setContentPane(new JPanel());
            BaseWindow.getInstance().revalidate();
        });

        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        bottomPanel.add(backButton);
        bottomPanel.add(startGameButton);

        add(splitPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode == null) return;
            Object obj = selectedNode.getUserObject();
            selectedGame = obj instanceof GameRoundPlayerDto game ? game : null;
        });

        loadLeaderboard(tableModel);

        SwingUtilities.invokeLater(() -> {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            double scale = Math.max(screenSize.width / 1920.0, screenSize.height / 1080.0);

            int baseTreeRowHeight = 24;
            int treeRowHeight = (int) (baseTreeRowHeight * scale);
            tree.setRowHeight(treeRowHeight);

            float baseFontSize = 14f;
            float fontSize = (float) (baseFontSize * scale);
            Font treeFont = tree.getFont().deriveFont(fontSize);
            tree.setFont(treeFont);

            renderer.setClosedIcon(new ImageIcon(folderIcon.getImage().getScaledInstance(treeRowHeight, treeRowHeight, Image.SCALE_SMOOTH)));
            renderer.setOpenIcon(new ImageIcon(openFolderIcon.getImage().getScaledInstance(treeRowHeight, treeRowHeight, Image.SCALE_SMOOTH)));
            renderer.setLeafIcon(new ImageIcon(fileIcon.getImage().getScaledInstance(treeRowHeight, treeRowHeight, Image.SCALE_SMOOTH)));

            startGameButton.setFont(startGameButton.getFont().deriveFont(fontSize));
            backButton.setFont(backButton.getFont().deriveFont(fontSize));

            leaderboardTable.setFont(leaderboardTable.getFont().deriveFont(fontSize * 0.9f));
            leaderboardTable.setRowHeight((int) (fontSize * 2.2));
            leaderboardTable.getTableHeader().setFont(leaderboardTable.getTableHeader().getFont().deriveFont(fontSize * 0.95f));
            leaderboardTable.getTableHeader().setPreferredSize(new Dimension(leaderboardTable.getWidth(), (int) (fontSize * 3)));
        });
    }

    private void loadLeaderboard(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        String sql = "SELECT concat(firstname, ' ', lastname) AS name, score FROM players p INNER JOIN player_tournament_info i ON p.id = i.player_id WHERE tournament_id = '" + tournamentDto.id + "' ORDER BY score DESC LIMIT 10;";
        List<HashMap<String, String>> result = DatabaseConnection.executeSql(sql);

        int rank = 1;
        if (result != null) {
            for (HashMap<String, String> row : result) {
                String name = row.get("name");
                String score = row.get("score");
                tableModel.addRow(new Object[]{rank++, name, score});
            }
        }
    }

    private static class GameRoundPlayerDto {
        GameDto gameDto;
        RoundDto roundDto;
        PlayerDto whitePlayer;
        PlayerDto blackPlayer;

        GameRoundPlayerDto(GameDto gameDto, RoundDto roundDto, PlayerDto whitePlayer, PlayerDto blackPlayer) {
            this.gameDto = gameDto;
            this.roundDto = roundDto;
            this.whitePlayer = whitePlayer;
            this.blackPlayer = blackPlayer;
        }

        @Override
        public String toString() {
            return switch (gameDto.result) {
                case 0 -> whitePlayer + " vs " + blackPlayer + " (½ - ½)";
                case 1 -> whitePlayer + " vs " + blackPlayer + " (1 - 0)";
                case -1 -> whitePlayer + " vs " + blackPlayer + " (0 - 1)";
                case null, default -> whitePlayer + " vs " + blackPlayer;
            };
        }
    }
}