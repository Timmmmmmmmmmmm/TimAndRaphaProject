package BoardGui;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class BoardPanel extends JPanel {

    Game game;
    JPanel boardPanel;
    JButton[][] board = new JButton[8][8];

    int selectedRow= -1, selectedColumn = -1;
    List<Move> legalMoves = new ArrayList<>();

    private final Map<String, Image> pieceImages = new HashMap<>();

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
        for (Move move : legalMoves) {
            if (move.fromRow == row && move.fromColumn == column) {
                System.out.println("Move");
                selectedRow = -1;
                selectedColumn = -1;
                refresh();
                return;
            }
        }

        if (game.board[row][column] != null) {
            selectedRow = row;
            selectedColumn = column;
            System.out.println("Select");
        } else {
            selectedRow = -1;
            selectedColumn = -1;
            System.out.println("deselect");
        }
        refresh();
    }

    void refresh() {
        int size = Math.min(getWidth(), getHeight());
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

        boardPanel.setBounds(x, y, size, size);
        boardPanel.doLayout();

        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {

                if (row == selectedRow && column == selectedColumn) {
                    board[row][column].setBackground(new Color(255, 162, 24));
                } else {
                    if ((row + column) % 2 == 0) {
                        board[row][column].setBackground(new Color(240, 217, 181));
                    } else {
                        board[row][column].setBackground(new Color(181, 136, 99));
                    }
                }

                Piece piece = game.board[row][column];
                if (piece == null) {
                    board[row][column].setIcon(null);
                } else {
                    String file = piece.white ? "images/pieces/white/" : "images/pieces/black/";
                    file = file + piece.getSymbol() + ".png";
                    Image img = getPieceImage(file);

                    int width = board[row][column].getWidth();
                    int height = board[row][column].getHeight();

                    if (width > 0 && height > 0) {
                        Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                        board[row][column].setIcon(new ImageIcon(scaled));
                    }
                }
            }
        }
    }

    @Override
    public void doLayout() {
        super.doLayout();
        refresh();
    }

    public Image getPieceImage(String piece) {
        if (!pieceImages.containsKey(piece)) {
            ImageIcon icon = new ImageIcon(Objects. requireNonNull(getClass().getResource(piece)));
            pieceImages.put(piece, icon.getImage());
        }
        return pieceImages.get(piece);
    }
}
