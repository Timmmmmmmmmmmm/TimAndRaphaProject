package Gui.panel;

import Gui.DatabaseConnection;
import Gui.dialog.NewPlayerDialog;
import Gui.dialog.NewTournamentDialog;
import Gui.dialog.OpenTournamentDialog;
import Gui.dto.PlayerDto;
import Gui.util.FideTitle;
import Gui.util.PlayerTableModel;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class StartTournamentPanel extends JPanel {

    private JTable table;
    private PlayerTableModel tableModel;
    private int hoverRow = -1;
    private int hoverCol = -1;

    public StartTournamentPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        setupTopPanel();
        setupTable();
    }

    private void setupTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton newPlayerButton = new JButton("New Player");
        JButton newTournamentButton = new JButton("New Tournament");
        JButton openTournamentButton = new JButton("Open Tournament");

        topPanel.add(newPlayerButton);
        topPanel.add(newTournamentButton);
        topPanel.add(openTournamentButton);

        add(topPanel, BorderLayout.NORTH);

        newPlayerButton.addActionListener(e -> {
            NewPlayerDialog.show();
            tableModel.reload();
        });
        newTournamentButton.addActionListener(e -> NewTournamentDialog.show());
        openTournamentButton.addActionListener(e -> OpenTournamentDialog.show());
    }

    private void setupTable() {
        tableModel = new PlayerTableModel();
        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setFillsViewportHeight(true);

        JComboBox<FideTitle> comboBox = new JComboBox<>(FideTitle.values());
        table.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(comboBox));

        table.getColumnModel().getColumn(7).setCellRenderer(new DeleteRenderer());

        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                hoverRow = table.rowAtPoint(e.getPoint());
                hoverCol = table.columnAtPoint(e.getPoint());
                table.repaint();
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoverRow = -1;
                hoverCol = -1;
                table.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (col == 7 && row >= 0) {
                    PlayerDto p = tableModel.getPlayerAt(row);
                    DatabaseConnection.executeSql("DELETE FROM players WHERE id = " + p.id);
                    tableModel.reload();
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    class DeleteRenderer extends JLabel implements TableCellRenderer {
        public DeleteRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Delete");
            if (row == hoverRow && column == hoverCol) {
                setBackground(new Color(239, 81, 81));
                setForeground(Color.WHITE);
            } else {
                setBackground(UIManager.getColor("Table.background"));
                setForeground(Color.BLACK);
            }
            return this;
        }
    }
}