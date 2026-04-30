package gui.panel;

import gui.server.ServerStartPanel;
import gui.BaseWindow;
import gui.DatabaseConnection;
import gui.dialog.NewPlayerDialog;
import gui.dialog.NewTournamentDialog;
import gui.dialog.OpenTournamentDialog;
import gui.dto.PlayerDto;
import gui.util.FideTitle;
import gui.util.PlayerTableModel;

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

        add(createMenuPanel(), BorderLayout.NORTH);
        setupTable();
    }

    public JPanel createMenuPanel() {
        JButton backButton = new JButton("Exit Tournament Menu");
        backButton.addActionListener(_ -> {
            BaseWindow.getInstance().setContentPane(new ServerStartPanel());
            BaseWindow.getInstance().revalidate();
            BaseWindow.getInstance().repaint();
        });

        JButton openTournamentButton = new JButton("Open Tournament");
        openTournamentButton.addActionListener(_ -> OpenTournamentDialog.show());

        JButton newTournamentButton = new JButton("New Tournament");
        newTournamentButton.addActionListener(_ -> NewTournamentDialog.show());

        JButton newPlayerButton = new JButton("New Player");
        newPlayerButton.addActionListener(_ -> {
            NewPlayerDialog.show();
            tableModel.reload();
        });

        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        menuPanel.add(backButton);
        menuPanel.add(openTournamentButton);
        menuPanel.add(newTournamentButton);
        menuPanel.add(newPlayerButton);
        return menuPanel;
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