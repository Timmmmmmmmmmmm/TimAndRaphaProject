package Gui;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/meinedb";
    private static final String USER = "user";
    private static final String PASSWORD = "pass";

    public static List<HashMap<String, String>> query(String sql) {
        List<HashMap<String, String>> result = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData meta = rs.getMetaData();
            int columns = meta.getColumnCount();

            while (rs.next()) {
                HashMap<String, String> row = new HashMap<>();
                for (int i = 1; i <= columns; i++) {
                    row.put(meta.getColumnName(i), rs.getString(i));
                }
                result.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static int update(String sql) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = con.createStatement()) {

            return stmt.executeUpdate(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
}