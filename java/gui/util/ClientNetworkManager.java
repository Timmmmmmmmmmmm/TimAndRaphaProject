package gui.util;

import gui.BaseWindow;
import gui.dialog.ResultDialog;
import gui.dto.GameInitDto;
import gui.panel.OnlineBoardPanel;

import java.net.Socket;
import java.util.function.Consumer;

public class ClientNetworkManager extends NetworkManager {

    private Consumer<GameInitDto> onGameInit;
    private Consumer<Move> onMove;

    public void setOnMove(Consumer<Move> onMove) {
        this.onMove = onMove;
    }

    public void setOnGameInit(Consumer<GameInitDto> onGameInit) {
        this.onGameInit = onGameInit;
    }

    public void connect(String host, int port) throws Exception {
        socket = new Socket(host, port);
        setup(socket);
        connected = true;
        System.out.println("[NETWORK] Connected to host");
    }

    public boolean isMyTurn(boolean whiteTurn) {
        return whiteTurn;
    }

    public void listen() {
        try {
            String msg;

            while ((msg = in.readLine()) != null && connected) {

                if (msg.startsWith("INIT:")) {
                    System.out.println("[NETWORK] Received INIT");
                    GameInitDto dto = parseInit(msg.substring(5));
                    if (onGameInit != null) onGameInit.accept(dto);
                    continue;
                }

                if (msg.equals("QUIT")) {
                    if (!selfDisconnect) {
                        System.out.println("[NETWORK] Host disconnected");
                        quit();
                    }
                    return;
                }

                if (msg.startsWith("END:")) {
                    System.out.println("[NETWORK] Received END (" + msg + ")");
                    endGame(GameResult.valueOf(msg.substring(6)), msg.charAt(4) == 'W');
                    return;
                }

                if (msg.startsWith("MOVE:")) {
                    Move move = parseMove(msg.substring(5));
                    System.out.println("[NETWORK] Received move (" + msg + ")");
                    if (onMove != null) onMove.accept(move);
                }
            }

        } catch (Exception ignored) {
            disconnect(false);
        }
    }

    private GameInitDto parseInit(String s) {
        if (s.isBlank()) return null;
        String[] p = s.split(";");

        if (Boolean.parseBoolean(p[0])) {
            return new GameInitDto(
                    Integer.parseInt(p[1]),
                    Integer.parseInt(p[2]));
        } else {
            return new GameInitDto(
                    Integer.parseInt(p[1]),
                    Integer.parseInt(p[2]),
                    p[3],
                    p[4],
                    Integer.parseInt(p[5]),
                    FideTitle.valueOf(p[6]),
                    p[7],
                    p[8],
                    Integer.parseInt(p[9]),
                    FideTitle.valueOf(p[10])
            );
        }
    }

    protected void endGame(GameResult result, boolean whiteWins) {
        super.endGame(result, whiteWins);

        OnlineBoardPanel panel = (OnlineBoardPanel) BaseWindow.getInstance().getContentPane();
        panel.timer.stop();
        ResultDialog.showGuestDialog(result, whiteWins);
    }

    public void sendMoveRequest(Move move) {
        if (out != null) {
            System.out.println("[NETWORK] Sending move request (" + serializeMoveRequest(move) + ")");
            out.println("REQUEST:" + serializeMoveRequest(move));
        }
    }

    private String serializeMoveRequest(Move m) {
        return m.fromRow + "," + m.fromColumn + "," + m.toRow + "," + m.toColumn;
    }
}