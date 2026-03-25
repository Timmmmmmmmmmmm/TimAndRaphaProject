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
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class TournamentPanel extends JPanel {

    private JTree tree;
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

        tree = new JTree(tournamentNode);
        tree.setRowHeight(28);

        JScrollPane scrollPane = new JScrollPane(tree);

        JButton startGameButton = new JButton("Game starten");
        startGameButton.setPreferredSize(new Dimension(150, 40));

        startGameButton.addActionListener(e -> {
            if (selectedGame != null) {
                if (selectedGame.gameDto.result == null) {
                    BaseWindow.getInstance().setContentPane(new BoardPanel(new Game(tournamentDto, selectedGame.roundDto, selectedGame.gameDto, selectedGame.whitePlayer, selectedGame.blackPlayer)));
                } else {
                    JOptionPane.showMessageDialog(this, "Spiel wurde bereits beendet!");
                }
            }
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(startGameButton);

        add(scrollPane, BorderLayout.CENTER);
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
    }

    public JTree getTree() {
        return tree;
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
                case null -> whitePlayer + " vs " + blackPlayer;
                case 0 -> whitePlayer + " vs " + blackPlayer + " (Draw)";
                case 1 -> whitePlayer + " 🏆 vs " + blackPlayer;
                case -1 -> whitePlayer + " vs " + blackPlayer + " 🏆";
                default -> whitePlayer + " vs " + blackPlayer;
            };
        }
    }
}