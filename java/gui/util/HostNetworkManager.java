package gui.util;

import gui.dialog.ResultDialog;
import gui.dto.GameInitDto;

import java.net.ServerSocket;
import java.util.function.Consumer;

public class HostNetworkManager extends NetworkManager {

    private Consumer<Move> onMoveRequest;
    private Runnable onConnected;

    public void setOnMoveRequest(Consumer<Move> onMoveRequest) {
        this.onMoveRequest = onMoveRequest;
    }

    public void setOnConnected(Runnable onConnected) {
        this.onConnected = onConnected;
    }

    public boolean isMyTurn(boolean whiteTurn) {
        return !whiteTurn;
    }

    public void startServer(int port) throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            socket = serverSocket.accept();
        }
        setup(socket);

        connected = true;
        System.out.println("[NETWORK] Guest connected");

        if (onConnected != null) onConnected.run();
    }

    public void listen() {
        try {
            String msg;

            while ((msg = in.readLine()) != null && connected) {

                if (msg.equals("QUIT")) {
                    if (!selfDisconnect) {
                        System.out.println("[NETWORK] Client disconnected");
                        quit();
                    }
                    return;
                }

                if (msg.startsWith("END:")) {
                    System.out.println("[NETWORK] Received END (" + msg + ")");
                    endGame(GameResult.valueOf(msg.substring(6)), msg.charAt(4) == 'W');
                    return;
                }

                if (msg.startsWith("REQUEST:")) {
                    Move move = parseMove(msg.substring(8));
                    System.out.println("[NETWORK] Received move request (" + msg + ")");
                    if (onMoveRequest != null) onMoveRequest.accept(move);
                }
            }

        } catch (Exception ignored) {
            disconnect(false);
        }
    }

    protected void endGame(GameResult result, boolean whiteWins) {
        super.endGame(result, whiteWins);
        onQuit.run();
        ResultDialog.showHostDialog(null, result, whiteWins);
    }

    public void sendMove(Move move) {
        if (out != null) {
            System.out.println("[NETWORK] Sending move (" + serializeMove(move) + ")");
            out.println("MOVE:" + serializeMove(move));
        }
    }

    private String serializeMove(Move m) {
        return m.fromRow + "," + m.fromColumn + "," + m.toRow + "," + m.toColumn;
    }

    public void sendInit(GameInitDto dto) {
        if (out != null) {
            out.println("INIT:" + serializeInit(dto));
            System.out.println("[NETWORK] Sending INIT");
        }
    }

    private String serializeInit(GameInitDto dto) {
        if (dto.isSimple) {
            return "true;" +
                    dto.base_consider_time + ";" +
                    dto.move_consider_time;
        } else {
            return "false;" +
                    dto.base_consider_time + ";" +
                    dto.move_consider_time + ";" +
                    dto.whiteFirstname + ";" +
                    dto.whiteLastname + ";" +
                    dto.whiteRating + ";" +
                    dto.whiteTitle.name() + ";" +
                    dto.blackFirstname + ";" +
                    dto.blackLastname + ";" +
                    dto.blackRating + ";" +
                    dto.blackTitle.name();
        }
    }
}