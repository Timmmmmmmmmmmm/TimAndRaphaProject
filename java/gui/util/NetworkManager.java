package gui.util;

import gui.BaseWindow;
import gui.panel.StartPanel;
import gui.util.GameResult;
import gui.util.Move;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class NetworkManager {

    protected BufferedReader in;
    protected PrintWriter out;
    protected Socket socket;

    protected boolean connected = false;
    protected volatile boolean selfDisconnect = false;

    public Runnable onQuit;

    protected void setup(Socket socket) throws Exception {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        new Thread(this::listen).start();
    }

    public abstract boolean isMyTurn(boolean whiteTurn);

    public abstract void listen();

    protected Move parseMove(String s) {
        String[] p = s.split(",");
        return new Move(
                Integer.parseInt(p[0]),
                Integer.parseInt(p[1]),
                Integer.parseInt(p[2]),
                Integer.parseInt(p[3])
        );
    }

    protected void quit() {
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

    protected void endGame(GameResult result, boolean whiteWins) {
        connected = false;
        try { if (socket != null) socket.close();
        } catch (Exception ignored) {}
    }

    public void disconnect(boolean sendQuit) {
        selfDisconnect = true;
        if (sendQuit && out != null) out.println("QUIT");

        connected = false;
        try { if (socket != null) socket.close();
        } catch (Exception ignored) {}

        BaseWindow.getInstance().setContentPane(new StartPanel());
        BaseWindow.getInstance().revalidate();
        BaseWindow.getInstance().repaint();
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