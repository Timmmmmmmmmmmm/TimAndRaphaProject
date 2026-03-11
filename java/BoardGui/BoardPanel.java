package BoardGui;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class BoardPanel extends JPanel {

    Game game;
    JPanel boardPanel;
    JButton[][] board = new JButton[8][8];

    public BoardPanel(Game game) {
        this.game = game;

        setLayout(new BorderLayout());

        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(8, 8));
        add(boardPanel, BorderLayout.CENTER);

        for (int row = 0; row < 8; row++)
            for (int column = 0; column < 8; column++) {

                JButton button = new JButton();

                button.setFont(new Font("Arial", Font.BOLD, 24));

                if ((row + column) % 2 == 0)
                    button.setBackground(new Color(240, 217, 181));
                else
                    button.setBackground(new Color(181, 136, 99));

                final int finalRow = row;
                final int finalColumn = column;

                button.addActionListener(_ -> click(finalRow, finalColumn));
                board[row][column] = button;
                boardPanel.add(button);
            }
        refresh();
    }

    public void click(int row, int column) {
        System.out.println("ROW: " + row + " COLUMN: " + column);
    }

    void refresh() {

        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {

                Piece p = game.board[r][c];

                if (p == null) {
                    board[r][c].setIcon(null);
                } else {
                    String file = p.white ? "pieces/white/" : "pieces/black/";
                    file = file + p.getSymbol() + ".png";
                    board[r][c].setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource(file))));
                }
            }
    }
}
