package gui.dialog;

import gui.BaseWindow;
import gui.dto.TournamentDto;
import gui.panel.TournamentPanel;
import gui.panel.TournamentResultPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class OpenTournamentDialog {

    public static void show () {
        JDialog dialog = new JDialog();
        dialog.setTitle("Open Tournament");
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(BaseWindow.getInstance());
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout());
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        List<TournamentDto> list = TournamentDto.getAsList("SELECT * FROM tournaments");
        assert list != null;
        JComboBox<TournamentDto> box = new JComboBox<>(list.toArray(new TournamentDto[0]));
        box.setPreferredSize(new Dimension(250, 30));

        JButton okButton = getOkButton(box, dialog);

        panel.add(box);
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(okButton, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private static JButton getOkButton(JComboBox<TournamentDto> box, JDialog dialog) {
        JButton ok = new JButton("OK");
        ok.addActionListener(_ -> {
            TournamentDto tournament = (TournamentDto) box.getSelectedItem();
            if (tournament != null) {
                if (tournament.status() == TournamentDto.TournamentStatus.COMPLETED) {
                    BaseWindow.getInstance().setContentPane(new TournamentResultPanel(tournament));
                } else {
                    BaseWindow.getInstance().setContentPane(new TournamentPanel(tournament));
                }
                BaseWindow.getInstance().revalidate();
                BaseWindow.getInstance().repaint();
                dialog.dispose();
            }
        });
        return ok;
    }
}
