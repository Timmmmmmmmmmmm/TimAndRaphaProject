package Gui.BoardGui;

import Gui.BaseWindow;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

public class BoardPanel extends JPanel {

    Game game;
    JPanel boardPanel;

    int whiteTime;
    PlayerDisplay whitePlayerDisplay;
    int blackTime;
    PlayerDisplay blackPlayerDisplay;

    JButton[][] board = new JButton[8][8];
    boolean inCheck = false;

    Timer timer;

    int selectedRow= -1, selectedColumn = -1;
    List<Move> legalMoves = new ArrayList<>();

    private final Map<String, Image> pieceImages = new HashMap<>();

    public BoardPanel(Game game) {
        this.game = game;
        whiteTime = game.tournamentDto.base_consider_time;
        blackTime = game.tournamentDto.base_consider_time;

        setLayout(new BorderLayout());

        createMenuBar();

        blackPlayerDisplay = new PlayerDisplay(game.blackPlayerDto, blackTime);
        blackPlayerDisplay.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(blackPlayerDisplay, BorderLayout.NORTH);

        whitePlayerDisplay = new PlayerDisplay(game.whitePlayerDto, whiteTime);
        whitePlayerDisplay.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(whitePlayerDisplay, BorderLayout.SOUTH);

        // Center Panel (Board)
        boardPanel = new JPanel();
        boardPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        boardPanel.setLayout(new GridLayout(8, 8));
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
                game.win(true);
                endGame(GameResult.TIME, false);
            }
            if (blackTime <= 0) {
                ((Timer) e.getSource()).stop();
                game.win(false);
                endGame(GameResult.TIME, true);
            }

            blackPlayerDisplay.updateTime(blackTime);
            whitePlayerDisplay.updateTime(whiteTime);
        });

        timer.start();
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
                            refresh();
                            endGame(GameResult.CHECKMATE, !game.whiteTurn);
                        } else {
                            refresh();
                            endGame(GameResult.STALEMATE, !game.whiteTurn);
                        }
                    }
                } else {
                    inCheck = false;
                }

                if (game.whiteTurn) {
                    blackTime += game.tournamentDto.move_consider_time;
                } else {
                    whiteTime += game.tournamentDto.move_consider_time;
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
        int minWindowSize = Math.min(getWidth(), getHeight());
        int northSouthHeight = getHeight() / 8;
        int centerSize = Math.min(minWindowSize, getHeight() - northSouthHeight * 2);

        boardPanel.setBounds((getWidth() - centerSize) / 2, (getHeight() - centerSize) / 2, centerSize, centerSize);
        blackPlayerDisplay.setBounds((getWidth() - centerSize) / 2, 0, centerSize, northSouthHeight);
        whitePlayerDisplay.setBounds((getWidth() - centerSize) / 2, getHeight() - northSouthHeight, centerSize, northSouthHeight);

        boardPanel.doLayout();
        blackPlayerDisplay.doLayout();
        whitePlayerDisplay.doLayout();

        blackPlayerDisplay.update(blackTime);
        whitePlayerDisplay.update(whiteTime);

        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {

                if (row == selectedRow && column == selectedColumn) {
                    board[row][column].setBackground(new Color(80, 124, 101));
                } else {
                    if ((row + column) % 2 == 0) {
                        board[row][column].setBackground(new Color(222, 227, 230));
                        board[row][column].setBorder(new BevelBorder(BevelBorder.LOWERED));
                    } else {
                        board[row][column].setBackground(new Color(140, 162, 173));
                        board[row][column].setBorder(new BevelBorder(BevelBorder.RAISED));
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

    public void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorderPainted(true);

        JMenu gameMenu = new JMenu("Game");
        JMenuItem drawItem = new JMenuItem("Draw");
        drawItem.addActionListener(_ -> {
            game.draw();
            endGame(GameResult.DRAW, true);
        });
        gameMenu.add(drawItem);
        menuBar.add(gameMenu);

        JMenu whiteMenu = new JMenu("White");
        JMenuItem whiteResignItem = new JMenuItem("Resign");
        whiteResignItem.addActionListener(_ -> {
            game.win(true);
            endGame(GameResult.RESIGN, false);
        });
        whiteMenu.add(whiteResignItem);
        menuBar.add(whiteMenu);

        JMenu blackMenu = new JMenu("Black");
        JMenuItem blackResignItem = new JMenuItem("Resign");
        blackResignItem.addActionListener(_ -> {
            game.win(false);
            endGame(GameResult.RESIGN, true);
        });
        blackMenu.add(blackResignItem);
        menuBar.add(blackMenu);

        BaseWindow.getInstance().setJMenuBar(menuBar);
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

    public void endGame(GameResult result, boolean whiteWins) {
        timer.stop();
        PGNWriter.saveMovesInDatabase(game);
        ResultDialog.showResult(game, result, whiteWins);
    }
}
