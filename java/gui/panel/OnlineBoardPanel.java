package gui.panel;

import gui.BaseWindow;
import gui.dialog.ResultDialog;
import gui.util.*;

import javax.swing.*;

public class OnlineBoardPanel extends BoardPanel {

    public NetworkManager network;
    boolean isHost;

    public OnlineBoardPanel(HostNetworkManager network, Game game) {
        super(game, true);
        isHost = true;
        this.game = game;
        this.network = network;

        initNetwork();
    }

    public OnlineBoardPanel(HostNetworkManager network, int base_consider_time, int move_consider_time) {
        super(base_consider_time, move_consider_time, true);
        isHost = true;
        this.network = network;
        initNetwork();
    }

    public OnlineBoardPanel(ClientNetworkManager network, int base_consider_time, int move_consider_time) {
        super(base_consider_time, move_consider_time, false);
        this.network = network;
        initNetwork();
    }

    public OnlineBoardPanel(ClientNetworkManager network, Game game) {
        super(game, false);
        this.network = network;
        initNetwork();
    }

    private void initNetwork() {
        if (isHost) {
            HostNetworkManager host = (HostNetworkManager) network;
            host.setOnMoveRequest(move -> SwingUtilities.invokeLater(() -> {
                makeMove(move);
                host.sendMove(move);
                refresh();
            }));
        } else {
            ClientNetworkManager guest = (ClientNetworkManager) network;
            guest.setOnMove(move -> SwingUtilities.invokeLater(() -> {
                makeMove(move);
                refresh();
            }));
        }
        network.onQuit = () -> timer.stop();
    }

    @Override
    public void click(int row, int column) {

        if (network.isMyTurn(game.whiteTurn)) return;

        for (Move move : legalMoves) {
            if (move.toRow == row && move.toColumn == column) {

                selectedRow = -1;
                selectedColumn = -1;

                if (isHost) {
                    HostNetworkManager host = (HostNetworkManager) network;
                    host.sendMove(move);
                    legalMoves.clear();
                    makeMove(move);
                } else {
                    ClientNetworkManager guest = (ClientNetworkManager) network;
                    guest.sendMoveRequest(move);
                }

                legalMoves.clear();
                refresh();
                return;
            }
        }

        super.click(row, column);
    }

    @Override
    public void addEndButtons(JPanel panel) {
        JButton draw = new JButton("Draw");
        draw.addActionListener(_ -> endGame(GameResult.DRAW, true));
        panel.add(draw);

        JButton b = new JButton("Resign");
        b.addActionListener(_ -> endGame(GameResult.RESIGN, false));
        panel.add(b);
    }

    @Override
    public JButton getBackButton() {
        JButton btn = new JButton("Exit game");
        btn.addActionListener(_ -> {
            if (timer != null) timer.stop();
            network.disconnect(true);
            if (isHost && game instanceof Game) {
                BaseWindow.getInstance().setContentPane(new TournamentPanel(((Game) game).tournamentDto));
            } else {
                BaseWindow.getInstance().setContentPane(new StartPanel());
            }

            BaseWindow.getInstance().revalidate();
            BaseWindow.getInstance().repaint();
        });
        return btn;
    }

    @Override
    public void endGame(GameResult result, boolean whiteWins) {
        if (isHost) {
            network.sendEnd(result, whiteWins);
            if (timer != null) timer.stop();

            if (game instanceof Game) {
                PGNWriter.saveMovesInDatabase((Game) game);
            }

            ResultDialog.showHostDialog(game, result, whiteWins);
        } else {
            if (result == GameResult.DRAW || result == GameResult.RESIGN) {
                network.sendEnd(result, whiteWins);
                if (timer != null) timer.stop();

                ResultDialog.showGuestDialog(result, whiteWins);
            } else {
                System.out.println("[NETWORK] End (" + result.name() + ") is invalid. Game is maybe out of sync");
            }
        }
    }
}