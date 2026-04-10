package Gui.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PGNReader {
    protected Game game;

    public PGNReader(Game game) {
        this.game = game;
    }

    protected void readPgnFile(String path) {
        List<String> moves = new ArrayList<>();
        StringBuilder moveText = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            boolean inMoveSection = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Skip empty lines and header tags
                if (line.isEmpty() || line.startsWith("[")) {
                    continue;
                }

                // Collect move text
                inMoveSection = true;
                moveText.append(line).append(" ");
            }

            if (inMoveSection) {
                moves = parseMoves(moveText.toString());
                saveMovesToDatabase(moves);
            }

        } catch (IOException e) {
            System.err.println("Error reading PGN file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<String> parseMoves(String moveText) {
        List<String> moves = new ArrayList<>();

        // Remove game result (1-0, 0-1, 1/2-1/2, *)
        moveText = moveText.replaceAll("(1-0|0-1|1/2-1/2|\\*)\\s*$", "");

        // Pattern to match move numbers and moves
        Pattern pattern = Pattern.compile("\\d+\\.\\s*([^\\s]+)(?:\\s+([^\\s]+))?");
        Matcher matcher = pattern.matcher(moveText);

        while (matcher.find()) {
            String whiteMove = matcher.group(1);
            String blackMove = matcher.group(2);

            if (whiteMove != null && !whiteMove.isEmpty()) {
                moves.add(whiteMove);
            }
            if (blackMove != null && !blackMove.isEmpty()) {
                moves.add(blackMove);
            }
        }

        return moves;
    }

    private void saveMovesToDatabase(List<String> moves) {
        String url = "jdbc:mysql://localhost:3306/chess_tournament";
        String user = "root";
        String password = "";

        String sql = "INSERT INTO games_moves (move_number, move, games_id) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < moves.size(); i++) {
                stmt.setInt(1, i + 1);
                stmt.setString(2, moves.get(i));
                stmt.addBatch();
            }

            stmt.executeBatch();
            System.out.println("Successfully saved " + moves.size() + " moves to database.");

        } catch (SQLException e) {
            System.err.println("Error saving moves to database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
