package gui.guest;

import gui.BaseWindow;
import gui.dto.GameInitDto;
import gui.panel.StartPanel;
import gui.util.FideTitle;
import gui.util.GameResult;
import gui.util.Move;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class GuestNetworkManager {

    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;

    private Consumer<GameInitDto> onGameInit;
    private Consumer<Move> onMove;

    private boolean connected = false;
    private volatile boolean selfDisconnect = false;

    public Runnable onQuit;

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

    private void setup(Socket socket) throws Exception {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        new Thread(this::listen).start();
    }

    public boolean isMyTurn(boolean whiteTurn) {
        return !whiteTurn;
    }

    // ----- INPUT -----

    private void listen() {
        try {
            String msg;

            while ((msg = in.readLine()) != null && connected) {

                if (msg.startsWith("INIT:")) {
                    System.out.println("[NETWORK] Received INIT");
                    GameInitDto dto = parseInit(msg.substring(5));
                    if (onGameInit != null) {
                        onGameInit.accept(dto);
                    }
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

                    if (onMove != null) {
                        onMove.accept(move);
                    }
                }
            }

        } catch (Exception ignored) {
            disconnect(false);
        }
    }

    private Move parseMove(String s) {
        String[] p = s.split(",");
        return new Move(
                Integer.parseInt(p[0]),
                Integer.parseInt(p[1]),
                Integer.parseInt(p[2]),
                Integer.parseInt(p[3])
        );
    }

    private GameInitDto parseInit(String s) {
        if (s.isBlank()) {
            return null;
        }
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

    private void quit() {
        connected = false;
        try { if (socket != null) socket.close();
        } catch (Exception ignored) {}

        onQuit.run();

        JOptionPane.showMessageDialog(
                BaseWindow.getInstance(),
                "Opponent disconnected",
                "Disconnect",
                JOptionPane.WARNING_MESSAGE
        );
        BaseWindow.getInstance().setContentPane(new StartPanel());
        BaseWindow.getInstance().revalidate();
        BaseWindow.getInstance().repaint();
    }

    private void endGame(GameResult result, boolean whiteWins) {
        connected = false;
        try { if (socket != null) socket.close();
        } catch (Exception ignored) {}

        GuestBoardPanel panel = (GuestBoardPanel) BaseWindow.getInstance().getContentPane();
        panel.timer.stop();
        GuestResultDialog.show(result, whiteWins);
    }

    public void disconnect(boolean sendQuit) {
        selfDisconnect = true;
        if (sendQuit) out.println("QUIT");

        connected = false;
        try { if (socket != null) socket.close();
        } catch (Exception ignored) {}

        BaseWindow.getInstance().setContentPane(new StartPanel());
        BaseWindow.getInstance().revalidate();
        BaseWindow.getInstance().repaint();
    }

    // ----- OUTPUT -----

    public void sendMoveRequest(Move move) {
        if (out != null) {
            System.out.println("[NETWORK] Sending move request (" + serializeMoveRequest(move) + ")");
            out.println("REQUEST:" + serializeMoveRequest(move));
        }
    }

    private String serializeMoveRequest(Move m) {
        return m.fromRow + "," + m.fromColumn + "," + m.toRow + "," + m.toColumn;
    }

    public void sendEnd(GameResult result, boolean whiteWins) {
        if (out != null) {
            String winner = whiteWins ? "W" : "B";
            String reason = result.name();

            System.out.println("[NETWORK] Sending END: " + reason + ":" + winner);
            out.println("END:" + winner + ":" + reason);
        }
    }
}