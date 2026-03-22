package Gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GuiTemplate<T> extends JPanel {
    private final Class<T> clazz;
    private final String tableName;
    private final DefaultTableModel model;

    public GuiTemplate(Class<T> clazz, String tableName) {
        this.clazz = clazz;
        this.tableName = tableName;

        setLayout(new BorderLayout());
        model = new DefaultTableModel();
        JTable table = new JTable(model);

        for (Field field : clazz.getDeclaredFields()) {
            model.addColumn(field.getName());
        }
        JButton addButton = new JButton("Add " + clazz.getSimpleName());
        addButton.addActionListener(_ -> openAddDialog());

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(addButton, BorderLayout.SOUTH);
    }

    private void loadData() {
        model.setRowCount(0);

        var rows = DatabaseConnection.query("SELECT * FROM " + tableName);

        for (var row : rows) {
            List<Object> values = new ArrayList<>();

            for (Field f : clazz.getDeclaredFields()) {
                values.add(row.get(f.getName()));
            }

            model.addRow(values.toArray());
        }
    }

    private void openAddDialog() {
        JDialog dialog = new JDialog((Frame) null, "Add " + clazz.getSimpleName(), true);
        dialog.setLayout(new GridLayout(0, 2));
        dialog.setLocationRelativeTo(null);

        List<JComponent> inputs = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();

        for (Field f : fields) {
            if (f.getName().equals("id")) continue;

            dialog.add(new JLabel(f.getName()));

            JComponent input;
            if (f.getType().isEnum()) {
                input = new JComboBox<>(f.getType().getEnumConstants());
            } else {
                input = new JTextField();
            }

            inputs.add(input);
            dialog.add(input);
        }

        JButton saveButton = getSaveButton(fields, inputs, dialog);

        dialog.add(saveButton);
        dialog.pack();
        dialog.setVisible(true);
    }

    private JButton getSaveButton(Field[] fields, List<JComponent> inputs, JDialog dialog) {
        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(_ -> {
            try {
                StringBuilder cols = new StringBuilder();
                StringBuilder vals = new StringBuilder();

                int inputIndex = 0;

                for (Field f : fields) {
                    if (f.getName().equals("id")) continue;

                    cols.append(f.getName()).append(",");
                    vals.append("'").append(getValue(inputs.get(inputIndex++), f)).append("',");
                }

                String sql = "INSERT INTO " + tableName +
                        " (" + cols.substring(0, cols.length() - 1) + ") VALUES (" +
                        vals.substring(0, vals.length() - 1) + ")";

                DatabaseConnection.update(sql);

                dialog.dispose();
                loadData();

            } catch (Exception ex) {
                System.out.println("SQL INSERT FAILED");
            }
        });
        return saveBtn;
    }

    private Object getValue(JComponent comp, Field f) {
        if (comp instanceof JTextField tf) {
            if (f.getType() == int.class) {
                return Integer.parseInt(tf.getText());
            }
            if (f.getType() == LocalDate.class) {
                return tf.getText();
            }
            return tf.getText();
        }

        if (comp instanceof JComboBox<?> cb) {
            return Objects.requireNonNull(cb.getSelectedItem()).toString();
        }

        return null;
    }
}
