package gui;

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
            System.out.println("[DATABASE] JDBC Connection failed to connect");
        }
    }

    public static List<HashMap<String, String>> executeSql(String sql) {
        System.out.println("[DATABASE] Executing SQL (" + sql + ")");
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            boolean hasResultSet = statement.execute(sql);

            if (hasResultSet) {
                List<HashMap<String, String>> result = getHashMaps(statement);
                return result.isEmpty() ? null : result;
            } else {
                return null;
            }

        } catch (SQLException e) {
            System.out.println("[DATABASE] Failed to execute SQL statement (" + e.getMessage() + " ~ " + e.getSQLState() + ")");
            return null;
        }
    }

    public static String insertAndReturnPrimaryKey(String sql) {
        System.out.println("[DATABASE] Executing SQL and return primary key (" + sql + ")");
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();

            if (resultSet.next()) {
                return resultSet.getString(1);
            } else {
                return null;
            }

        } catch (SQLException e) {
            System.out.println("[DATABASE] Failed to execute SQL statement (" + e.getMessage() + " ~ " + e.getSQLState() + ")");
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