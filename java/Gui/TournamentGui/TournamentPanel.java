package Gui.TournamentGui;

import Gui.Dto.GameDto;
import Gui.Dto.PlayerDto;
import Gui.Dto.RoundDto;
import Gui.Dto.TournamentDto;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TournamentPanel extends JPanel {

    private JTree tree;

    public TournamentPanel(TournamentDto tournamentDto) {
        setLayout(new BorderLayout());

        // Root-Knoten = Turnier
        DefaultMutableTreeNode tournamentNode = new DefaultMutableTreeNode(tournamentDto.name);

        // Runden aus DB holen
        List<RoundDto> rounds = RoundDto.getAsList(
                "SELECT * FROM rounds WHERE tournament_id = " + tournamentDto.id + " ORDER BY round_number;"
        );

        if (rounds != null) {
            for (RoundDto roundDto : rounds) {
                DefaultMutableTreeNode roundNode = new DefaultMutableTreeNode("Round " + roundDto.round_number);

                // Games für diese Runde
                List<GameDto> games = GameDto.getAsList(
                        "SELECT * FROM games WHERE round_id = " + roundDto.id + ";"
                );

                if (games != null) {
                    List<GameDtoWithPlayers> gamesWithPlayers = new ArrayList<>();
                    for (GameDto gameDto : games) {
                        PlayerDto whitePlayer = Objects.requireNonNull(PlayerDto.getAsList("SELECT * FROM players WHERE id = " + gameDto.player_white + ";")).getFirst();
                        PlayerDto blackPlayer = Objects.requireNonNull(PlayerDto.getAsList("SELECT * FROM players WHERE id = " + gameDto.player_black + ";")).getFirst();
                        GameDtoWithPlayers gameWithPlayers = new GameDtoWithPlayers(gameDto, whitePlayer, blackPlayer);
                        gamesWithPlayers.add(gameWithPlayers);
                        DefaultMutableTreeNode gameNode = new DefaultMutableTreeNode(gameWithPlayers.whitePlayer.toString() + " | " + gameWithPlayers.blackPlayer.toString());
                        roundNode.add(gameNode);
                    }
                }

                tournamentNode.add(roundNode);
            }
        }

        tree = new JTree(tournamentNode);
        JScrollPane scrollPane = new JScrollPane(tree);
        add(scrollPane, BorderLayout.CENTER);

        // TreeSelectionListener für Klicks
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode =
                        (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (selectedNode == null) return;

                Object userObject = selectedNode.getUserObject();
                if (userObject instanceof GameDtoWithPlayers game) {
                    // Nur Game-Knoten reagieren
                    System.out.println("Game clicked! ID = " + game.whitePlayer.firstname);

                    // TODO: Hier Spiel starten / Dialog öffnen / Ergebnis eingeben
                }
            }
        });
    }

    public JTree getTree() {
        return tree;
    }

    private class GameDtoWithPlayers {
        GameDto gameDto;
        PlayerDto whitePlayer;
        PlayerDto blackPlayer;

        GameDtoWithPlayers(GameDto gameDto, PlayerDto whitePlayer, PlayerDto blackPlayer) {
            this.gameDto = gameDto;
            this.whitePlayer = whitePlayer;
            this.blackPlayer = blackPlayer;
        }
    }
}