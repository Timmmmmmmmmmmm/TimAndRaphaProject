package gui.server;

import gui.BaseWindow;

import javax.swing.*;

public class ServerWindow extends JFrame {

    static void main() {
        SwingUtilities.invokeLater(() -> new BaseWindow(true));
    }
}