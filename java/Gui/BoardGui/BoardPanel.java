package Gui.BoardGui;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.Border;
import java.awt.*;
import java.util.*;
import java.util.List;

public class BoardPanel extends JPanel {

    Game game;
    JPanel boardPanel;
    JPanel northPanel;
    JPanel southPanel;

    JButton draw;
    int whiteTime;
    JLabel whiteLabel;
    JButton whiteResign;
    int blackTime;
    JLabel blackLabel;
    JButton blackResign;

    Border boardPanelBorder;

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
        setBackground(new Color(80, 80, 80));

        // North Panel
        northPanel = new JPanel();
        northPanel.setBackground(new Color(80, 80, 80));
        add(northPanel, BorderLayout.NORTH);

        draw = new JButton();
        draw.setText("DRAW");
        draw.addActionListener(_ -> endGame(GameResult.DRAW, true));
        northPanel.add(draw);

        blackLabel = new JLabel("BLACK: " + formatTime(blackTime));
        northPanel.add(blackLabel);

        blackResign = new JButton("BLACK RESIGN");
        blackResign.addActionListener(_ -> endGame(GameResult.RESIGN, true));
        northPanel.add(blackResign);

        // South Panel
        southPanel = new JPanel();
        southPanel.setBackground(new Color(80, 80, 80));
        add(southPanel, BorderLayout.SOUTH);

        whiteLabel = new JLabel("WHITE: " + formatTime(whiteTime));
        southPanel.add(whiteLabel);

        whiteResign = new JButton("WHITE RESIGN");
        whiteResign.addActionListener(_ -> endGame(GameResult.RESIGN, false));
        southPanel.add(whiteResign);

        // Center Panel (Board)
        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(8, 8));
        boardPanelBorder = BorderFactory.createLineBorder(game.whiteTurn ? Color.white : Color.black, 3);
        boardPanel.setBorder(boardPanelBorder);
        add(boardPanel, BorderLayout.CENTER);

        for (int row = 0; row < 8; row++)
            for (int column = 0; column < 8; column++) {

                JButton button = new JButton();

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

                boardPanelBorder = BorderFactory.createLineBorder(game.whiteTurn ? Color.white : Color.black, 3);
                boardPanel.setBorder(boardPanelBorder);

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
                    board[row][column].setBackground(new Color(80, 124, 101));
                } else {
                    if ((row + column) % 2 == 0) {
                        board[row][column].setBackground(new Color(222, 227, 230));
                    } else {
                        board[row][column].setBackground(new Color(140, 162, 173));
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
            board[move.toRow][move.toColumn].setBackground(new Color(155, 199, 0));
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
        System.out.println(white ? "White wins" : "Black wins");
        timer.stop();
        ResultDialog.showResult(game, result, white);
    }
}
