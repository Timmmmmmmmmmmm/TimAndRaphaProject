package gui.client;

import gui.BaseWindow;

import javax.swing.*;

public class ClientWindow extends JFrame {

    static void main() {
        SwingUtilities.invokeLater(() -> new BaseWindow(false));
    }
}