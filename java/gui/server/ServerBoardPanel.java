package gui.server;

import gui.BaseWindow;
import gui.panel.BoardPanel;
import gui.panel.TournamentPanel;
import gui.util.Game;
import gui.util.GameResult;
import gui.util.Move;
import gui.util.PGNWriter;

import javax.swing.*;

public class ServerBoardPanel extends BoardPanel {

    public final ServerNetworkManager network;

    public ServerBoardPanel(ServerNetworkManager network, Game game) {
        super(game);
        this.game = game;
        this.network = network;

        initNetwork();
    }

    public ServerBoardPanel(ServerNetworkManager network, int base_consider_time, int move_consider_time) {
        super(base_consider_time, move_consider_time);
        this.network = network;
        initNetwork();
    }

    private void initNetwork() {

        network.setOnMoveRequest(move -> SwingUtilities.invokeLater(() -> {
            makeMove(move);
            network.sendMove(move);
            refresh();
        }));
    }

    @Override
    public void click(int row, int column) {

        if (!network.isMyTurn(game.whiteTurn)) return;

        for (Move move : legalMoves) {
            if (move.toRow == row && move.toColumn == column) {

                selectedRow = -1;
                selectedColumn = -1;


                network.sendMove(move);
                legalMoves.clear();
                makeMove(move);
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
            if (game instanceof Game) {
                BaseWindow.getInstance().setContentPane(new TournamentPanel(((Game) game).tournamentDto));
            } else {
                BaseWindow.getInstance().setContentPane(new ServerStartPanel());
            }

            BaseWindow.getInstance().revalidate();
            BaseWindow.getInstance().repaint();
        });
        return btn;
    }

    @Override
    public void endGame(GameResult result, boolean whiteWins) {
        network.sendEnd(result, whiteWins);
        if (timer != null) timer.stop();

        if (game instanceof Game) {
            PGNWriter.saveMovesInDatabase((Game) game);
        }

        ServerResultDialog.show(game, result, whiteWins);
    }
}