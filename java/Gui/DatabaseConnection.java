package Gui;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/schachturnierverwaltung?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "pass";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Der JDBC Treiber konnte nicht geladen werden.");
        }
    }
    
    public static List<HashMap<String, String>> executeSql(String sql) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = con.createStatement()) {

            System.out.println("Executing SQL: " + sql);
            boolean hasResultSet = stmt.execute(sql);

            if (hasResultSet) {
                List<HashMap<String, String>> result = getHashMaps(stmt);
                return result.isEmpty() ? null : result;
            } else {
                return null;
            }

        } catch (SQLException e) {
            System.out.println("Die Datenbankabfrage ist fehlgeschlagen: " + e.getSQLState());
            return null;
        }
    }

    private static List<HashMap<String, String>> getHashMaps(Statement stmt) throws SQLException {
        ResultSet rs = stmt.getResultSet();
        List<HashMap<String, String>> result = new ArrayList<>();
        ResultSetMetaData meta = rs.getMetaData();
        int columns = meta.getColumnCount();

        while (rs.next()) {
            HashMap<String, String> row = new HashMap<>();
            for (int i = 1; i <= columns; i++) {
                row.put(meta.getColumnName(i), rs.getString(i));
            }
            result.add(row);
        }
        return result;
    }
}