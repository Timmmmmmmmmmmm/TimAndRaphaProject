package gui.panel;

import gui.BaseWindow;
import gui.dto.PlayerDto;
import gui.util.*;
import gui.dialog.ResultDialog;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.util.*;
import java.util.List;

public class BoardPanel extends JPanel {

    final SimpleGame game;
    final int base_consider_time;
    final int move_consider_time;

    JPanel boardPanel;

    int whiteTime;
    PlayerDisplay whitePlayerDisplay;

    int blackTime;
    PlayerDisplay blackPlayerDisplay;

    final JButton[][] board = new JButton[8][8];

    boolean inCheck = false;

    Timer timer;

    int selectedRow = -1, selectedColumn = -1;
    List<Move> legalMoves = new ArrayList<>();

    private final Map<String, Image> pieceImages = new HashMap<>();

    final boolean isComplexGame;

    public BoardPanel(int base_consider_time, int move_consider_time) {
        isComplexGame = false;
        game = new SimpleGame();
        this.base_consider_time = base_consider_time;
        this.move_consider_time = move_consider_time;
        setupUI();
        setupPlayerDisplays();
    }

    public BoardPanel(Game game) {
        isComplexGame = true;
        this.game = game;
        base_consider_time = game.tournamentDto.base_consider_time();
        move_consider_time = game.tournamentDto.move_consider_time();
        setupUI();
        setupPlayerDisplays(game.blackPlayerDto, game.whitePlayerDto);
    }

    public void setupUI() {
        whiteTime = base_consider_time;
        blackTime = move_consider_time;

        setLayout(new BorderLayout());

        boardPanel = new JPanel();
        boardPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        boardPanel.setLayout(new GridLayout(8, 8));
        add(boardPanel, BorderLayout.CENTER);

        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                JButton button = new JButton();
                final int finalRow = row;
                final int finalColumn = column;

                button.addActionListener(_ -> click(finalRow, finalColumn));
                button.setUI(new BasicButtonUI());

                board[row][column] = button;
                boardPanel.add(button);
            }
        }

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

    public JPanel createMenuPanel() {
        JButton backButton = new JButton("Exit game");
        backButton.addActionListener(_ -> {
            if (game instanceof Game) {
                BaseWindow.getInstance().setContentPane(
                        new TournamentPanel(((Game) game).tournamentDto)
                );
            } else {
                BaseWindow.getInstance().setContentPane(new StartPanel());
            }
            BaseWindow.getInstance().revalidate();
            BaseWindow.getInstance().repaint();
        });

        JButton drawButton = new JButton("Draw");
        drawButton.addActionListener(_ -> endGame(GameResult.DRAW, true));

        JButton blackResignButton = new JButton("Black resign");
        blackResignButton.addActionListener(_ -> endGame(GameResult.RESIGN, true));

        JButton whiteResignButton = new JButton("White resign");
        whiteResignButton.addActionListener(_ -> endGame(GameResult.RESIGN, false));

        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        menuPanel.add(backButton);
        menuPanel.add(drawButton);
        menuPanel.add(whiteResignButton);
        menuPanel.add(blackResignButton);

        return menuPanel;
    }

    public void setupPlayerDisplays(PlayerDto blackPlayerDto, PlayerDto whitePlayerDto) {
        blackPlayerDisplay = new PlayerDisplay(blackPlayerDto, blackTime);
        blackPlayerDisplay.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(blackPlayerDisplay, BorderLayout.NORTH);

        whitePlayerDisplay = new PlayerDisplay(whitePlayerDto, whiteTime);
        whitePlayerDisplay.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(whitePlayerDisplay, BorderLayout.SOUTH);
    }

    public void setupPlayerDisplays() {
        blackPlayerDisplay = new PlayerDisplay("Black Player", blackTime);
        blackPlayerDisplay.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(blackPlayerDisplay, BorderLayout.NORTH);

        whitePlayerDisplay = new PlayerDisplay("White Player", whiteTime);
        whitePlayerDisplay.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(whitePlayerDisplay, BorderLayout.SOUTH);
    }

    public void click(int row, int column) {
        for (Move move : legalMoves) {
            if (move.toRow == row && move.toColumn == column) {

                selectedRow = -1;
                selectedColumn = -1;

                ChessResult result = game.makeMove(move, false);

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
                    blackTime += move_consider_time;
                } else {
                    whiteTime += move_consider_time;
                }

                legalMoves.clear();
                refresh();
                return;
            }
        }

        if (game.board[row][column] != null &&
                game.board[row][column].white == game.whiteTurn) {

            selectedRow = row;
            selectedColumn = column;
            legalMoves = MoveGenerator.generateLegal(game, row, column);

        } else {
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

        boardPanel.setBounds(
                (getWidth() - centerSize) / 2,
                (getHeight() - centerSize) / 2,
                centerSize,
                centerSize
        );

        blackPlayerDisplay.setBounds(
                (getWidth() - centerSize) / 2,
                0,
                centerSize,
                northSouthHeight
        );

        whitePlayerDisplay.setBounds(
                (getWidth() - centerSize) / 2,
                getHeight() - northSouthHeight,
                centerSize,
                northSouthHeight
        );

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
                        board[row][column].setBorder(
                                new BevelBorder(BevelBorder.LOWERED)
                        );
                    } else {
                        board[row][column].setBackground(new Color(140, 162, 173));
                        board[row][column].setBorder(
                                new BevelBorder(BevelBorder.RAISED)
                        );
                    }
                }

                Piece piece = game.board[row][column];

                if (piece != null && inCheck &&
                        piece.white == game.whiteTurn &&
                        piece.type == Piece.Type.KING) {

                    board[row][column].setBackground(new Color(239, 81, 81));
                }

                if (piece == null) {
                    board[row][column].setIcon(null);
                } else {
                    String file = "/gui/assets/pieces/" + piece.getSymbol() + ".png";
                    Image img = getPieceImage(file);

                    int width = board[row][column].getWidth();
                    int height = board[row][column].getHeight();

                    if (width > 0 && height > 0) {
                        Image scaled = img.getScaledInstance(
                                width, height, Image.SCALE_SMOOTH
                        );
                        board[row][column].setIcon(new ImageIcon(scaled));
                    }
                }
            }
        }

        highlight();
    }

    public void highlight() {
        for (Move move : legalMoves) {
            board[move.toRow][move.toColumn]
                    .setBackground(new Color(155, 199, 0));
        }
    }

    @Override
    public void doLayout() {
        super.doLayout();
        refresh();
    }

    public Image getPieceImage(String piece) {
        if (!pieceImages.containsKey(piece)) {
            ImageIcon icon = new ImageIcon(
                    Objects.requireNonNull(getClass().getResource(piece))
            );
            pieceImages.put(piece, icon.getImage());
        }
        return pieceImages.get(piece);
    }

    public void endGame(GameResult result, boolean whiteWins) {
        timer.stop();

        if (game instanceof Game) {
            PGNWriter.saveMovesInDatabase((Game) game);
        }

        ResultDialog.show(game, result, whiteWins);
    }
}