package gui;

import gui.guest.GuestBoardPanel;
import gui.guest.GuestNetworkManager;
import gui.host.HostBoardPanel;
import gui.host.HostNetworkManager;
import gui.panel.StartPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;
import java.util.Random;

public class BaseWindow extends JFrame {

    private static BaseWindow instance;

    public BaseWindow(boolean isServer) {
        instance = this;
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.updateComponentTreeUI(this);
        if (isServer) {
            setContentPane(new StartPanel());
            setTitle("ChessManager");
        }
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int) (screenSize.width * 0.65), (int) (screenSize.height * 0.65));
        setMinimumSize(new Dimension(screenSize.width / 4, screenSize.height / 4));
        setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                if (isServer) {
                    Container contentPane = getContentPane();

                    if (contentPane instanceof HostBoardPanel) {
                        HostNetworkManager network = ((HostBoardPanel) contentPane).network;
                        network.disconnect(true);
                    }
                } else {
                    Container contentPane = getContentPane();

                    if (contentPane instanceof GuestBoardPanel) {
                        GuestNetworkManager network = ((GuestBoardPanel) contentPane).network;
                        network.disconnect(true);
                    }
                }

                System.exit(0);
            }
        });
        setResizable(true);
        setVisible(true);
        revalidate();
        repaint();

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/gui/assets/analysis/brilliant.png")));
        setIconImage(icon.getImage());

        Timer timer = getIconTimer();
        timer.start();
    }

    private Timer getIconTimer() {
        Random random = new Random();
        return new Timer(10000, new ActionListener() {
            String lastAnalysis = "brilliant";
            @Override
            public void actionPerformed(ActionEvent e) {
                String analysis = switch (random.nextInt(8)) {
                    case 0 -> lastAnalysis.equals("best") ? "blunder" : "best";
                    case 1 -> lastAnalysis.equals("blunder") ? "brilliant" : "blunder";
                    case 2 -> lastAnalysis.equals("brilliant") ? "critical" : "brilliant";
                    case 3 -> lastAnalysis.equals("critical") ? "excellent" : "critical";
                    case 4 -> lastAnalysis.equals("excellent") ? "inaccuracy" : "excellent";
                    case 5 -> lastAnalysis.equals("inaccuracy") ? "mistake" : "inaccuracy";
                    case 6 -> lastAnalysis.equals("mistake") ? "okay" : "mistake";
                    case 7 -> lastAnalysis.equals("okay") ? "theory" : "okay";
                    default -> lastAnalysis.equals("theory") ? "best" : "theory";
                };
                lastAnalysis = analysis;
                ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/gui/assets/analysis/" + analysis + ".png")));
                setIconImage(icon.getImage());
            }
        });
    }

    static void main() {
        SwingUtilities.invokeLater(() -> new BaseWindow(true));
    }

    public static BaseWindow getInstance() {
        return instance;
    }
}