package gui.util;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class PGNReader {

    public static List<Move> readMoves() {
        return readMoves(new SimpleGame());
    }

    public static List<Move> readMoves(SimpleGame game) {
        File file = chooseFile();
        if (file == null) return Collections.emptyList();

        StringBuilder moveText = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("[")) continue;
                moveText.append(line).append(" ");
            }

        } catch (IOException e) {
            return new ArrayList<>();
        }

        return parseMoves(moveText.toString(), game);
    }

    private static File chooseFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(System.getProperty("user.home") + "/Downloads"));
        chooser.setFileFilter(new FileNameExtensionFilter("PGN Files (*.pgn)", "pgn"));

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }

    private static List<Move> parseMoves(String moveText, SimpleGame game) {
        List<Move> moves = new ArrayList<>();

        moveText = moveText.replaceAll("(1-0|0-1|1/2-1/2|\\*)\\s*$", "");

        Pattern pattern = Pattern.compile("\\d+\\.\\s*(\\S+)(?:\\s+(\\S+))?");
        Matcher matcher = pattern.matcher(moveText);

        SimpleGame replay = game.copy();

        while (matcher.find()) {
            String white = matcher.group(1);
            String black = matcher.group(2);

            if (white != null) {
                Move m = resolveMove(replay, clean(white));
                moves.add(m);
                replay.makeMove(m, true);
            }

            if (black != null) {
                Move m = resolveMove(replay, clean(black));
                moves.add(m);
                replay.makeMove(m, true);
            }
        }

        return moves;
    }

    private static String clean(String move) {
        return move.replaceAll("[!?]+", "");
    }

    private static Move resolveMove(SimpleGame game, String san) {
        List<Move> allMoves = new ArrayList<>();

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (game.board[r][c] != null && game.board[r][c].white == game.whiteTurn) {
                    allMoves.addAll(MoveGenerator.generateLegal(game, r, c));
                }
            }
        }

        for (Move m : allMoves) {
            SimpleGame copy = game.copy();
            copy.makeMove(m, false);

            String last = copy.history.getLast();

            if (normalize(last).equals(normalize(san))) {
                return m;
            }
        }

        throw new RuntimeException("Move not found: " + san);
    }

    private static String normalize(String s) {
        return s.replaceAll("[+#]", "");
    }
}