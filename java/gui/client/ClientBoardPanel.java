package gui.client;

import gui.BaseWindow;
import gui.panel.BoardPanel;
import gui.util.Game;
import gui.util.GameResult;
import gui.util.Move;

import javax.swing.*;

public class ClientBoardPanel extends BoardPanel {

    public final ClientNetworkManager network;

    public ClientBoardPanel(ClientNetworkManager network, int base_consider_time, int move_consider_time) {
        super(base_consider_time, move_consider_time);
        this.network = network;
        initNetwork();
    }

    public ClientBoardPanel(ClientNetworkManager network, Game game) {
        super(game);
        this.network = network;
        initNetwork();
    }

    private void initNetwork() {

        network.setOnMove(move -> SwingUtilities.invokeLater(() -> {
            makeMove(move);
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

                network.sendMoveRequest(move);
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
        b.addActionListener(_ -> endGame(GameResult.RESIGN, true));
        panel.add(b);
    }

    @Override
    public JButton getBackButton() {
        JButton btn = new JButton("Exit game");
        btn.addActionListener(_ -> {
            if (timer != null) timer.stop();
            network.disconnect(true);
            BaseWindow.getInstance().setContentPane(new ClientStartPanel());

            BaseWindow.getInstance().revalidate();
            BaseWindow.getInstance().repaint();
        });
        return btn;
    }

    @Override
    public void endGame(GameResult result, boolean whiteWins) {
        if (result == GameResult.DRAW || result == GameResult.RESIGN) {
            network.sendEnd(result, whiteWins);
            if (timer != null) timer.stop();

            ClientResultDialog.show(result, whiteWins);
        } else {
            System.out.println("[NETWORK] End (" + result.name() + ") is invalid. Game is maybe out of sync");
        }
    }
}