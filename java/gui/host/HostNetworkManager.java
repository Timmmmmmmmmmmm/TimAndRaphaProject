package gui.host;

import gui.BaseWindow;
import gui.dto.GameInitDto;
import gui.panel.StartPanel;
import gui.util.GameResult;
import gui.util.Move;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public class HostNetworkManager {

    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;

    private Consumer<Move> onMoveRequest;
    private Runnable onConnected;

    public Runnable onQuit;

    private boolean connected = false;
    private volatile boolean selfDisconnect = false;

    public void setOnMoveRequest(Consumer<Move> onMoveRequest) {
        this.onMoveRequest = onMoveRequest;
    }

    public void setOnConnected(Runnable onConnected) {
        this.onConnected = onConnected;
    }

    public boolean isMyTurn(boolean whiteTurn) {
        return whiteTurn;
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

    private void setup(Socket socket) throws Exception {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        new Thread(this::listen).start();
    }

    // ----- INPUT -----

    private void listen() {
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

                    if (onMoveRequest != null) {
                        onMoveRequest.accept(move);
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

        onQuit.run();
        HostResultDialog.show(null, result, whiteWins);
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

    public void sendEnd(GameResult result, boolean whiteWins) {
        if (out != null) {
            String winner = whiteWins ? "W" : "B";
            String reason = result.name();

            System.out.println("[NETWORK] Sending END: " + reason + ":" + winner);
            out.println("END:" + winner + ":" + reason);
        }
    }
}