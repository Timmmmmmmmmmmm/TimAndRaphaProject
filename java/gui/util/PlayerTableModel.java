package gui.util;

import gui.DatabaseConnection;
import gui.dto.PlayerDto;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class PlayerTableModel extends AbstractTableModel {

    private List<PlayerDto> players;
    private final String[] columns = {"ID","Firstname","Lastname","Rating","Title","Gender","Birthdate",""};

    public PlayerTableModel() { reload(); }

    public void reload() {
        players = PlayerDto.getAsList("SELECT * FROM players");
        fireTableDataChanged();
    }

    public PlayerDto getPlayerAt(int row) { return players.get(row); }

    @Override public int getRowCount() { return players == null ? 0 : players.size(); }
    @Override public int getColumnCount() { return columns.length; }
    @Override public String getColumnName(int column) { return columns[column]; }
    @Override public boolean isCellEditable(int row, int col) { return col != 0; }

    @Override
    public Object getValueAt(int row, int col) {
        PlayerDto p = players.get(row);
        return switch (col) {
            case 0 -> p.id;
            case 1 -> p.firstname;
            case 2 -> p.lastname;
            case 3 -> p.fide_rating;
            case 4 -> p.fide_title;
            case 5 -> p.gender;
            case 6 -> p.birthdate;
            case 7 -> "Delete";
            default -> null;
        };
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        PlayerDto p = players.get(row);
        String sql = null;
        switch (col) {
            case 1 -> { p.firstname = value.toString(); sql = "firstname='" + p.firstname + "'"; }
            case 2 -> { p.lastname = value.toString(); sql = "lastname='" + p.lastname + "'"; }
            case 3 -> { p.fide_rating = Integer.parseInt(value.toString()); sql = "fide_rating=" + p.fide_rating; }
            case 4 -> { p.fide_title = (FideTitle) value; sql = "fide_title='" + p.fide_title.getKey() + "'"; }
            case 5 -> { p.gender = value.toString().charAt(0); sql = "gender='" + p.gender + "'"; }
            case 6 -> { p.birthdate = DateUtil.parseLocalDate(value.toString()); sql = "birthdate='" + p.birthdate + "'"; }
        }
        if (sql != null) DatabaseConnection.executeSql("UPDATE players SET " + sql + " WHERE id=" + p.id);
        fireTableRowsUpdated(row, row);
    }
}