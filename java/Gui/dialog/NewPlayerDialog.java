package Gui.dialog;

import Gui.DatabaseConnection;
import Gui.dto.PlayerDto;
import Gui.util.FideTitle;
import Gui.util.PlayerTableModel;

import javax.swing.*;

import javax.swing.*;
import java.awt.*;

public class NewPlayerDialog {

    public static void show() {
        JDialog dialog = new JDialog();
        dialog.setTitle("New Player");
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);
        JPanel panel = createFormPanel();

        JTextField first = new JTextField();
        JTextField last = new JTextField();
        JTextField rating = new JTextField();
        JComboBox<FideTitle> title = new JComboBox<>(FideTitle.values());
        JTextField gender = new JTextField();
        JTextField birth = new JTextField();

        addField(panel, "Firstname", first);
        addField(panel, "Lastname", last);
        addField(panel, "Rating", rating);
        addField(panel, "Title", title);
        addField(panel, "Gender", gender);
        addField(panel, "Birthdate", birth);

        JButton ok = new JButton("OK");
        ok.addActionListener(e -> {
            String sql = "INSERT INTO players (firstname, lastname, fide_rating, fide_title, gender, birthdate) VALUES (" +
                    "'" + first.getText() + "'," +
                    "'" + last.getText() + "'," +
                    rating.getText() + "," +
                    "'" + ((FideTitle)title.getSelectedItem()).getKey() + "'," +
                    "'" + gender.getText() + "'," +
                    "'" + birth.getText() + "')";
            DatabaseConnection.executeSql(sql);
            dialog.dispose();
        });

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(ok, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private static JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(0,1,5,5));
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        return panel;
    }



    private static void addField(JPanel panel, String name, JComponent comp) {
        panel.add(new JLabel(name));
        panel.add(comp);
    }
}
