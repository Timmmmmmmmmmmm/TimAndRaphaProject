package gui.panel;

import gui.BaseWindow;
import gui.dto.PlayerDto;
import gui.util.*;
import gui.dialog.ResultDialog;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class BoardPanel extends JPanel {

    private BufferedImage backgroundImage;

    final SimpleGame game;
    final boolean isReplay;
    List<Move> replayMoves = new ArrayList<>();

    int base_consider_time = -1;
    int move_consider_time = -1;
    int blackTime;
    int whiteTime;

    JPanel mainPanel;
    JPanel boardPanel;
    PlayerDisplay blackPlayerDisplay;
    PlayerDisplay whitePlayerDisplay;

    final JButton[][] board = new JButton[8][8];

    boolean inCheck = false;

    Timer timer;

    int selectedRow = -1, selectedColumn = -1;
    List<Move> legalMoves = new ArrayList<>();

    private final Map<String, Image> pieceImages = new HashMap<>();

    public BoardPanel(int base_consider_time, int move_consider_time) {
        game = new SimpleGame();
        isReplay = false;
        this.base_consider_time = base_consider_time;
        this.move_consider_time = move_consider_time;
        setupUI();
        setupPlayerDisplays();
    }

    public BoardPanel(List<Move> replayMoves) {
        game = new SimpleGame();
        isReplay = true;
        this.replayMoves = replayMoves;
        setupUI();
        setupPlayerDisplays();
    }

    public BoardPanel(Game game) {
        this.game = game;
        isReplay = false;
        base_consider_time = game.tournamentDto.base_consider_time();
        move_consider_time = game.tournamentDto.move_consider_time();
        setupUI();
        setupPlayerDisplays(game.blackPlayerDto, game.whitePlayerDto);
    }

    public BoardPanel(Game game, List<Move> replayMoves) {
        this.game = game;
        isReplay = true;
        this.replayMoves = replayMoves;
        setupUI();
        setupPlayerDisplays(game.whitePlayerDto, game.blackPlayerDto);
    }

    public void setupUI() {
        try {
            backgroundImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/gui/assets/chessBackground.png")));
        } catch (Exception ignored) {
        }

        whiteTime = base_consider_time;
        blackTime = base_consider_time;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(createMenuPanel(), BorderLayout.NORTH);

        mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(0, 0, 0, 0));
        add(mainPanel, BorderLayout.CENTER);

        boardPanel = new JPanel();
        boardPanel.setBorder(BorderFactory.createRaisedSoftBevelBorder());
        boardPanel.setLayout(new GridLayout(8, 8));

        mainPanel.add(boardPanel, BorderLayout.CENTER);

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

        if (!isReplay) {
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
                repaint();
            });

            timer.start();
        }
    }

    public JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        menuPanel.setBackground(new Color(0, 0, 0, 0));

        JButton backButton = getBackButton();
        menuPanel.add(backButton);

        if (isReplay) {
            JButton nextMoveButton = new JButton("Next move");
            nextMoveButton.addActionListener(_ -> {
                game.makeMove(replayMoves.get(game.moveCounter), false);
                refresh();
            });
            menuPanel.add(nextMoveButton);
        } else {
            JButton drawButton = new JButton("Draw");
            drawButton.addActionListener(_ -> endGame(GameResult.DRAW, true));
            menuPanel.add(drawButton);

            JButton blackResignButton = new JButton("Black resign");
            blackResignButton.addActionListener(_ -> endGame(GameResult.RESIGN, true));
            menuPanel.add(blackResignButton);

            JButton whiteResignButton = new JButton("White resign");
            whiteResignButton.addActionListener(_ -> endGame(GameResult.RESIGN, false));
            menuPanel.add(whiteResignButton);
        }

        return menuPanel;
    }

    private JButton getBackButton() {
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
        return backButton;
    }

    public void setupPlayerDisplays(PlayerDto blackPlayerDto, PlayerDto whitePlayerDto) {
        blackPlayerDisplay = new PlayerDisplay(blackPlayerDto, blackTime);
        blackPlayerDisplay.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.add(blackPlayerDisplay, BorderLayout.NORTH);

        whitePlayerDisplay = new PlayerDisplay(whitePlayerDto, whiteTime);
        whitePlayerDisplay.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.add(whitePlayerDisplay, BorderLayout.SOUTH);
    }

    public void setupPlayerDisplays() {
        blackPlayerDisplay = new PlayerDisplay("Black Player", blackTime);
        blackPlayerDisplay.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.add(blackPlayerDisplay, BorderLayout.NORTH);

        whitePlayerDisplay = new PlayerDisplay("White Player", whiteTime);
        whitePlayerDisplay.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.add(whitePlayerDisplay, BorderLayout.SOUTH);
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
                            legalMoves.clear();
                            refresh();
                            endGame(GameResult.CHECKMATE, !game.whiteTurn);
                        } else {
                            legalMoves.clear();
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
        int minWindowSize = Math.min(mainPanel.getWidth(), mainPanel.getHeight());
        int northSouthHeight = mainPanel.getHeight() / 8;
        int centerSize = Math.min(minWindowSize, mainPanel.getHeight() - northSouthHeight * 2);

        boardPanel.setBounds(
                (mainPanel.getWidth() - centerSize) / 2,
                (mainPanel.getHeight() - centerSize) / 2,
                centerSize,
                centerSize
        );

        blackPlayerDisplay.setBounds(
                (mainPanel.getWidth() - centerSize) / 2,
                0,
                centerSize,
                northSouthHeight
        );

        whitePlayerDisplay.setBounds(
                (mainPanel.getWidth() - centerSize) / 2,
                mainPanel.getHeight() - northSouthHeight,
                centerSize,
                northSouthHeight
        );

        blackPlayerDisplay.update(blackTime);
        whitePlayerDisplay.update(whiteTime);

        int size = Math.min(boardPanel.getWidth(), boardPanel.getHeight()) / 8;

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
                    Image scaled = img.getScaledInstance(size, size, Image.SCALE_SMOOTH);
                    board[row][column].setIcon(new ImageIcon(scaled));
                }

            }
        }

        highlight();
        repaint();
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            int panelWidth = getWidth();
            int panelHeight = getHeight();

            int imgWidth = backgroundImage.getWidth(this);
            int imgHeight = backgroundImage.getHeight(this);

            double scale = Math.max(
                    (double) panelWidth / imgWidth,
                    (double) panelHeight / imgHeight
            );

            int newWidth = (int) (imgWidth * scale);
            int newHeight = (int) (imgHeight * scale);

            int x = (panelWidth - newWidth) / 2;
            int y = (panelHeight - newHeight) / 2;

            g.drawImage(backgroundImage, x, y, newWidth, newHeight, null);
        }
    }
}