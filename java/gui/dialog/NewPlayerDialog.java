package gui.dialog;

import gui.BaseWindow;
import gui.DatabaseConnection;
import gui.util.FideTitle;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.regex.Pattern;

public class NewPlayerDialog {

    public static void show() {
        JDialog dialog = new JDialog((Frame) null, "New Player", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel firstLabel = new JLabel("Firstname");
        JLabel lastLabel = new JLabel("Lastname");
        JLabel ratingLabel = new JLabel("Rating");
        JLabel titleLabel = new JLabel("Title");
        JLabel genderLabel = new JLabel("Gender");
        JLabel birthLabel = new JLabel("Birthdate");

        JTextField first = new JTextField();
        JTextField last = new JTextField();
        JTextField rating = new JTextField();
        JComboBox<FideTitle> title = new JComboBox<>(FideTitle.values());
        JTextField gender = new JTextField();
        JTextField birth = new JTextField();

        first.setMaximumSize(new Dimension(200, 25));
        last.setMaximumSize(new Dimension(200, 25));
        rating.setMaximumSize(new Dimension(200, 25));
        gender.setMaximumSize(new Dimension(200, 25));
        birth.setMaximumSize(new Dimension(200, 25));

        first.setAlignmentX(Component.CENTER_ALIGNMENT);
        last.setAlignmentX(Component.CENTER_ALIGNMENT);
        rating.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        gender.setAlignmentX(Component.CENTER_ALIGNMENT);
        birth.setAlignmentX(Component.CENTER_ALIGNMENT);

        ((AbstractDocument) rating.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("\\d+")) super.insertString(fb, offset, string, attr);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("\\d*")) super.replace(fb, offset, length, text, attrs);
            }
        });

        ((AbstractDocument) gender.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("[a-zA-Z]")) super.insertString(fb, offset, string, attr);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("[a-zA-Z]")) super.replace(fb, offset, length, text, attrs);
            }
        });

        JButton ok = new JButton("OK");
        ok.setEnabled(false);
        ok.setAlignmentX(Component.CENTER_ALIGNMENT);

        Pattern isoPattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
        Pattern dePattern = Pattern.compile("\\d{2}\\.\\d{2}\\.\\d{4}");

        Runnable validate = () -> {
            boolean firstOk = !first.getText().isBlank();
            boolean lastOk = !last.getText().isBlank();
            boolean ratingOk = rating.getText().matches("\\d+");
            boolean genderOk = gender.getText().length() == 1;
            boolean titleOk = title.getSelectedItem() != null;

            boolean birthOk = false;
            String b = birth.getText().trim();

            if (isoPattern.matcher(b).matches()) {
                try {
                    LocalDate.parse(b);
                    birthOk = true;
                } catch (DateTimeParseException ignored) {}
            } else if (dePattern.matcher(b).matches()) {
                try {
                    DateTimeFormatter f = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                    LocalDate.parse(b, f);
                    birthOk = true;
                } catch (DateTimeParseException ignored) {}
            }

            ok.setEnabled(firstOk && lastOk && ratingOk && genderOk && titleOk && birthOk);
        };

        DocumentListener listener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { validate.run(); }
            public void removeUpdate(DocumentEvent e) { validate.run(); }
            public void changedUpdate(DocumentEvent e) { validate.run(); }
        };

        first.getDocument().addDocumentListener(listener);
        last.getDocument().addDocumentListener(listener);
        rating.getDocument().addDocumentListener(listener);
        gender.getDocument().addDocumentListener(listener);
        birth.getDocument().addDocumentListener(listener);

        ok.addActionListener(_ -> {
            String b = birth.getText().trim();
            LocalDate date;

            if (b.matches("\\d{2}\\.\\d{2}\\.\\d{4}")) {
                date = LocalDate.parse(b, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            } else {
                date = LocalDate.parse(b);
            }

            String sql = "INSERT INTO players (firstname, lastname, fide_rating, fide_title, gender, birthdate) VALUES (" +
                    "'" + first.getText() + "'," +
                    "'" + last.getText() + "'," +
                    Integer.parseInt(rating.getText()) + "," +
                    "'" + ((FideTitle) Objects.requireNonNull(title.getSelectedItem())).getKey() + "'," +
                    "'" + gender.getText().charAt(0) + "'," +
                    "'" + date + "')";

            DatabaseConnection.executeSql(sql);
            dialog.dispose();
        });

        panel.add(firstLabel);
        panel.add(first);
        panel.add(Box.createVerticalStrut(5));

        panel.add(lastLabel);
        panel.add(last);
        panel.add(Box.createVerticalStrut(5));

        panel.add(ratingLabel);
        panel.add(rating);
        panel.add(Box.createVerticalStrut(5));

        panel.add(titleLabel);
        panel.add(title);
        panel.add(Box.createVerticalStrut(5));

        panel.add(genderLabel);
        panel.add(gender);
        panel.add(Box.createVerticalStrut(5));

        panel.add(birthLabel);
        panel.add(birth);
        panel.add(Box.createVerticalStrut(10));

        panel.add(ok);

        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(BaseWindow.getInstance());
        dialog.setVisible(true);
    }
}