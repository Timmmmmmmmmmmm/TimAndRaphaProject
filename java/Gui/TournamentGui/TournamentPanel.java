package Gui.TournamentGui;

import Gui.BaseWindow;
import Gui.BoardGui.BoardPanel;
import Gui.BoardGui.Game;
import Gui.Dto.GameDto;
import Gui.Dto.PlayerDto;
import Gui.Dto.RoundDto;
import Gui.Dto.TournamentDto;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Objects;

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

    public TournamentPanel(TournamentDto tournamentDto) {
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
                        PlayerDto whitePlayer = Objects.requireNonNull(
                                PlayerDto.getAsList("SELECT * FROM players WHERE id = " + gameDto.player_white + ";")
                        ).getFirst();

                        PlayerDto blackPlayer = Objects.requireNonNull(
                                PlayerDto.getAsList("SELECT * FROM players WHERE id = " + gameDto.player_black + ";")
                        ).getFirst();

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
        JScrollPane leaderboardScrollPane = new JScrollPane(leaderboardTable);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                treeScrollPane,
                leaderboardScrollPane
        );

        startGameButton = new JButton("Game starten");
        backButton = new JButton("Zurück");

        startGameButton.addActionListener(e -> {
            if (selectedGame != null) {
                if (selectedGame.gameDto.result == null) {
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
                } else {
                    JOptionPane.showMessageDialog(this, "Spiel wurde bereits beendet!");
                }
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
            DefaultMutableTreeNode selectedNode =
                    (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode == null) return;

            Object obj = selectedNode.getUserObject();

            if (obj instanceof GameRoundPlayerDto game) {
                selectedGame = game;
            } else {
                selectedGame = null;
            }
        });

        SwingUtilities.invokeLater(() -> {
            int width = splitPane.getWidth();
            int dividerSize = splitPane.getDividerSize();
            splitPane.setDividerLocation((width - dividerSize) / 2);

            int h = getHeight();

            if (h > 0) {
                int size = h / 25;

                tree.setRowHeight(size);
                tree.setFont(tree.getFont().deriveFont((float) size));

                renderer.setClosedIcon(new ImageIcon(folderIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH)));
                renderer.setOpenIcon(new ImageIcon(openFolderIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH)));
                renderer.setLeafIcon(new ImageIcon(fileIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH)));

                float buttonFont = h / 30f;
                startGameButton.setFont(startGameButton.getFont().deriveFont(buttonFont));
                backButton.setFont(backButton.getFont().deriveFont(buttonFont));
                leaderboardTable.setFont(leaderboardTable.getFont().deriveFont(buttonFont * 0.8f));
                leaderboardTable.setRowHeight((int) (buttonFont * 1.5));
            }
        });
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