package Gui.BoardGui;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.util.*;
import java.util.List;

public class BoardPanel extends JPanel {

    Game game;
    JPanel boardPanel;
    JPanel northPanel;
    JLabel turn;
    JButton draw;
    int whiteTime;
    JLabel whiteLabel;
    JButton whiteResign;
    int blackTime;
    JLabel blackLabel;
    JButton blackResign;

    JButton[][] board = new JButton[8][8];
    boolean inCheck = false;

    Timer timer;

    int selectedRow= -1, selectedColumn = -1;
    List<Move> legalMoves = new ArrayList<>();

    private final Map<String, Image> pieceImages = new HashMap<>();

    public BoardPanel(Game game) {
        this.game = game;
        whiteTime = game.tournament.base_consider_time;
        blackTime = game.tournament.base_consider_time;

        setLayout(new BorderLayout());

        northPanel = new JPanel();
        add(northPanel, BorderLayout.NORTH);

        turn = new JLabel();
        turn.setText(game.whiteTurn ? "WHITE" : "BLACK");
        northPanel.add(turn);

        draw = new JButton();
        draw.setText("DRAW");
        draw.addActionListener(e -> endGame(GameResult.DRAW, true));
        northPanel.add(draw);

        whiteLabel = new JLabel();
        whiteLabel.setText("WHITE: " + formatTime(whiteTime));
        northPanel.add(whiteLabel);

        blackLabel = new JLabel();
        blackLabel.setText("BLACK: " + formatTime(blackTime));
        northPanel.add(blackLabel);

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

        timer = new Timer(1000, e -> {
            if (game.whiteTurn) {
                whiteTime--;
            } else {
                blackTime--;
            }
            if (whiteTime <= 0) {
                ((Timer) e.getSource()).stop();
            }
            if (blackTime <= 0) {
                ((Timer) e.getSource()).stop();
            }
            whiteLabel.setText("WHITE: " + formatTime(whiteTime));
            blackLabel.setText("BLACK: " + formatTime(blackTime));
        });

        timer.start();
    }

    private String formatTime(int seconds) {
        int minutes = (seconds - (seconds % 60)) / 60;
        seconds -= (seconds - (seconds % 60));
        return minutes + ":" + (seconds < 10 ? "0" + seconds : seconds);
    }

    public void click(int row, int column) {
        for (Move move : legalMoves) {
            if (move.toRow == row && move.toColumn == column) {
                //Move
                selectedRow = -1;
                selectedColumn = -1;
                ChessResult result = game.makeMove(move, false);
                //From here whiteTurn is flipped
                if (result != null && result != ChessResult.NONE) {
                    if (result == ChessResult.CHECK) {
                        inCheck = true;
                    } else {
                        if (result == ChessResult.CHECKMATE) {
                            endGame(GameResult.CHECKMATE, !game.whiteTurn);
                        } else {
                            endGame(GameResult.STALEMATE, !game.whiteTurn);
                        }
                    }
                } else {
                    inCheck = false;
                }
                turn.setText(game.whiteTurn ? "WHITE" : "BLACK");
                if (game.whiteTurn) {
                    blackTime += game.tournament.move_consider_time;
                } else {
                    whiteTime += game.tournament.move_consider_time;
                }
                legalMoves.clear();

                refresh();
                return;
            }
        }

        if (game.board[row][column] != null && game.board[row][column].white == game.whiteTurn) {
            //Select
            selectedRow = row;
            selectedColumn = column;
            legalMoves = MoveGenerator.generateLegal(game, row, column);
        } else {
            //Deselect
            selectedRow = -1;
            selectedColumn = -1;
            legalMoves.clear();
        }
        refresh();
    }

    void refresh() {
        int size = Math.min(getWidth(), getHeight());
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

        //northPanel.setSize(northPanel.getWidth(), getHeight() / 5);
        northPanel.setSize(northPanel.getWidth(), getHeight() / 7);
        boardPanel.setBounds(x + (northPanel.getHeight() / 2), y + northPanel.getHeight(), size - northPanel.getHeight(), size - northPanel.getHeight());
        boardPanel.doLayout();
        northPanel.doLayout();

        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {

                if (row == selectedRow && column == selectedColumn) {
                    board[row][column].setBackground(new Color(234, 182, 103));
                } else {
                    if ((row + column) % 2 == 0) {
                        board[row][column].setBackground(new Color(240, 217, 181));
                    } else {
                        board[row][column].setBackground(new Color(181, 136, 99));
                    }
                }

                Piece piece = game.board[row][column];
                if (piece != null && inCheck && piece.white == game.whiteTurn && piece.type == Piece.Type.KING) {
                    board[row][column].setBackground(new Color(239, 81, 81));
                }

                if (piece == null) {
                    board[row][column].setIcon(null);
                } else {
                    String file = "images/pieces/";
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
        highlight();
    }

    public void highlight() {
        for (Move move : legalMoves) {
            board[move.toRow][move.toColumn].setBackground(new Color(98,153, 36));
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

    public void endGame(GameResult result, boolean white) {
        timer.stop();
        ResultDialog.showResult(game, result, white);
    }
}
