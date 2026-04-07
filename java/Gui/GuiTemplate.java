package Gui;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class GuiTemplate<T> extends JPanel {
    private final Class<T> clazz;
    private final String tableName;
    private final DefaultTableModel model;
    private final JTable table;
    private boolean isUpdating = false;
    private final Field[] fields;

    public GuiTemplate(Class<T> clazz, String tableName) {
        this.clazz = clazz;
        this.tableName = tableName;
        this.fields = clazz.getDeclaredFields();

        setLayout(new BorderLayout());

        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Delete-Spalte und ID-Spalte (Spalte 0) nicht editierbar
                return column != 0 && column != getColumnCount() - 1;
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);

        // DELETE per Klick
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (row < 0 || col < 0) return;

                if (col == model.getColumnCount() - 1) {
                    Object id = table.getValueAt(row, 0);

                    DatabaseConnection.executeSql(
                            "DELETE FROM " + tableName + " WHERE id=" + id
                    );

                    model.removeRow(row);
                }
            }
        });

        // Spalten
        for (Field field : fields) {
            model.addColumn(field.getName());
        }
        model.addColumn("Delete");

        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton addButton = new JButton("Add " + clazz.getSimpleName());
        addButton.addActionListener(_ -> openAddDialog());
        add(addButton, BorderLayout.SOUTH);

        model.addTableModelListener(e -> {
            if (isUpdating) return;
            if (e.getType() != TableModelEvent.UPDATE) return;

            int row = e.getFirstRow();
            int col = e.getColumn();

            if (row < 0 || col < 0) return;
            if (col == model.getColumnCount() - 1) return;

            Field field = fields[col];

            Object id = model.getValueAt(row, 0);
            Object newValue = model.getValueAt(row, col);

            if (id == null || field.getName().equals("id")) return;

            try {
                isUpdating = true;

                // 🔥 Typkonvertierung
                if (field.getType() == int.class) {
                    newValue = Integer.parseInt(newValue.toString());
                }

                if (field.getType() == LocalDate.class) {
                    String normalized = normalizeDate(newValue.toString());

                    // Nur setzen wenn sich wirklich was geändert hat!
                    if (!normalized.equals(newValue)) {
                        model.setValueAt(normalized, row, col);
                    }

                    newValue = normalized;
                }

                String sql = "UPDATE " + tableName +
                        " SET " + field.getName() + "='" + newValue + "'" +
                        " WHERE id=" + id;

                DatabaseConnection.executeSql(sql);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(table, "Ungültiger Wert!");
                loadData();
            } finally {
                isUpdating = false;
            }
        });

        loadData();
        setupEditors();
    }

    private void loadData() {
        model.setRowCount(0);

        List<HashMap<String, String>> rows =
                DatabaseConnection.executeSql("SELECT * FROM " + tableName);

        if (rows == null) return;

        for (HashMap<String, String> row : rows) {
            List<Object> values = new ArrayList<>();

            for (Field f : fields) {
                values.add(row.get(f.getName()));
            }

            values.add("Remove");
            model.addRow(values.toArray());
        }

        table.getColumn("Remove").setCellRenderer(new ButtonRenderer("delete.png"));
        table.getColumn("Remove").setMaxWidth(40);
    }

    private void setupEditors() {
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];

            // ENUM
            if (field.getType().isEnum()) {
                JComboBox<Object> combo =
                        new JComboBox<>(field.getType().getEnumConstants());
                table.getColumnModel().getColumn(i)
                        .setCellEditor(new DefaultCellEditor(combo));
            }

            // INT
            if (field.getType() == int.class) {
                JFormattedTextField intField =
                        new JFormattedTextField(NumberFormat.getIntegerInstance());
                intField.setBorder(null);

                table.getColumnModel().getColumn(i)
                        .setCellEditor(new DefaultCellEditor(intField));
            }
        }
    }

    private String normalizeDate(String input) {
        try {
            input = input.trim();

            if (input.contains(".")) {
                String[] p = input.split("\\.");

                int d = Integer.parseInt(p[0]);
                int m = Integer.parseInt(p[1]);
                int y = Integer.parseInt(p[2]);

                if (y < 100) y += (y < 50) ? 2000 : 1900;

                return LocalDate.of(y, m, d).toString();
            }

            return LocalDate.parse(input).toString();

        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    private void openAddDialog() {
        JDialog dialog = new JDialog((Frame) null,
                "Add " + clazz.getSimpleName(), true);

        dialog.setLayout(new GridLayout(0, 2));
        dialog.setLocationRelativeTo(null);

        List<JComponent> inputs = new ArrayList<>();

        for (Field f : fields) {
            if (f.getName().equals("id")) continue;

            dialog.add(new JLabel(f.getName()));
            JComponent input;

            if (f.getType().isEnum()) {
                input = new JComboBox<>(f.getType().getEnumConstants());
            } else if (f.getType() == int.class) {
                input = new JFormattedTextField(NumberFormat.getIntegerInstance());
            } else {
                JTextField tf = new JTextField();
                if (f.getType() == LocalDate.class) {
                    tf.setToolTipText("TT.MM.JJ oder YYYY-MM-DD");
                }
                input = tf;
            }

            inputs.add(input);
            dialog.add(input);
        }

        JButton save = new JButton("Save");
        save.addActionListener(_ -> {
            try {
                StringBuilder cols = new StringBuilder();
                StringBuilder vals = new StringBuilder();

                int i = 0;
                for (Field f : fields) {
                    if (f.getName().equals("id")) continue;

                    Object val = getValue(inputs.get(i++), f);

                    if (f.getType() == LocalDate.class) {
                        val = normalizeDate(val.toString());
                    }

                    cols.append(f.getName()).append(",");
                    vals.append("'").append(val).append("',");
                }

                String sql = "INSERT INTO " + tableName +
                        " (" + cols.substring(0, cols.length() - 1) + ")" +
                        " VALUES (" + vals.substring(0, vals.length() - 1) + ")";

                DatabaseConnection.executeSql(sql);

                dialog.dispose();
                loadData();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Ungültige Eingabe!");
            }
        });

        dialog.add(save);
        dialog.pack();
        dialog.setVisible(true);
    }

    private Object getValue(JComponent comp, Field f) {
        if (comp instanceof JTextField tf) {
            return tf.getText();
        }

        if (comp instanceof JComboBox<?> cb) {
            return Objects.requireNonNull(cb.getSelectedItem());
        }

        return null;
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer(String icon) {
            Image img = new ImageIcon(getClass().getResource(icon))
                    .getImage()
                    .getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            setIcon(new ImageIcon(img));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            return this;
        }
    }
}