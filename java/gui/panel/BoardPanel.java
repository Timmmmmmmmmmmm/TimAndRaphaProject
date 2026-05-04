package gui.panel;

import gui.BaseWindow;
import gui.host.HostResultDialog;
import gui.dto.PlayerDto;
import gui.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class BoardPanel extends JPanel {

    private BufferedImage backgroundImage;

    public SimpleGame game;
    final boolean isReplay;
    List<Move> replayMoves = new ArrayList<>();

    int base_consider_time;
    int move_consider_time;
    int blackTime;
    int whiteTime;

    JPanel mainPanel;
    JPanel boardPanel;
    PlayerDisplay blackPlayerDisplay;
    PlayerDisplay whitePlayerDisplay;

    JButton[][] board = new JButton[8][8];

    boolean inCheck = false;

    public Timer timer;

    public boolean playingAsWhite = true;

    public int selectedRow = -1, selectedColumn = -1;
    public List<Move> legalMoves = new ArrayList<>();

    private final Map<String, Image> pieceImages = new HashMap<>();

    public BoardPanel(int base_consider_time, int move_consider_time, boolean playingAsWhite) {
        game = new SimpleGame();
        this.playingAsWhite = playingAsWhite;
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
        base_consider_time = -1;
        setupUI();
        setupPlayerDisplays();
    }

    public BoardPanel(Game game, boolean playingAsWhite) {
        this.game = game;
        this.playingAsWhite = playingAsWhite;
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
        base_consider_time = -1;
        setupUI();
        setupPlayerDisplays(game.whitePlayerDto, game.blackPlayerDto);
    }

    public void setupUI() {
        try {
            backgroundImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/gui/assets/chessBackground.png")));
        } catch (Exception ignored) {}

        whiteTime = base_consider_time;
        blackTime = base_consider_time;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(createMenuPanel(), BorderLayout.NORTH);

        mainPanel = new JPanel(null);
        mainPanel.setBackground(new Color(0, 0, 0, 0));
        add(mainPanel, BorderLayout.CENTER);

        boardPanel = new JPanel(new GridLayout(8, 8));
        boardPanel.setBorder(BorderFactory.createRaisedSoftBevelBorder());
        mainPanel.add(boardPanel);

        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                JButton button = new JButton();
                final int r = playingAsWhite ? row : 7 - row;
                final int c = playingAsWhite ? column : 7 - column;
                button.addActionListener(_ -> click(r, c));
                button.setUI(new BasicButtonUI());
                if (playingAsWhite) {
                    board[row][column] = button;
                } else {
                    board[7 - row][7 - column] = button;
                }
                boardPanel.add(button);
            }
        }

        if (!isReplay) {
            timer = new Timer(1000, _ -> tick());
            timer.setInitialDelay(1000);
            timer.start();
        }
    }

    private void tick() {
        if (whiteTime <= 0 || blackTime <= 0) return;

        if (game.whiteTurn) {
            whiteTime--;
        } else {
            blackTime--;
        }

        if (whiteTime <= 0) {
            timer.stop();
            game.result = "1-0";
            endGame(GameResult.TIME, false);
            return;
        }

        if (blackTime <= 0) {
            timer.stop();
            game.result = "0-1";
            endGame(GameResult.TIME, true);
            return;
        }

        blackPlayerDisplay.updateTime(blackTime);
        whitePlayerDisplay.updateTime(whiteTime);
        repaint();
    }

    public JPanel createMenuPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(0,0,0,0));

        panel.add(getBackButton());

        if (isReplay) {
            JButton next = new JButton("Next move");
            next.addActionListener(_ -> {
                makeMove(replayMoves.get(game.moveCounter));
                refresh();
            });
            panel.add(next);
        } else {
            addEndButtons(panel);
        }

        return panel;
    }

    public void addEndButtons(JPanel panel) {
        JButton draw = new JButton("Draw");
        draw.addActionListener(_ -> endGame(GameResult.DRAW, true));
        panel.add(draw);

        JButton b = new JButton("Black resign");
        b.addActionListener(_ -> endGame(GameResult.RESIGN, true));
        panel.add(b);

        JButton w = new JButton("White resign");
        w.addActionListener(_ -> endGame(GameResult.RESIGN, false));
        panel.add(w);
    }

    public JButton getBackButton() {
        JButton btn = new JButton("Exit game");
        btn.addActionListener(_ -> {
            if (timer != null) timer.stop();

            if (game instanceof Game) {
                BaseWindow.getInstance().setContentPane(new TournamentPanel(((Game) game).tournamentDto));
            } else {
                BaseWindow.getInstance().setContentPane(new StartPanel());
            }

            BaseWindow.getInstance().revalidate();
            BaseWindow.getInstance().repaint();
        });
        return btn;
    }

    public void setupPlayerDisplays(PlayerDto black, PlayerDto white) {
        blackPlayerDisplay = new PlayerDisplay(black, blackTime);
        blackPlayerDisplay.setBorder(new EmptyBorder(10,10,10,10));
        mainPanel.add(blackPlayerDisplay);

        whitePlayerDisplay = new PlayerDisplay(white, whiteTime);
        whitePlayerDisplay.setBorder(new EmptyBorder(10,10,10,10));
        mainPanel.add(whitePlayerDisplay);
    }

    public void setupPlayerDisplays() {
        blackPlayerDisplay = new PlayerDisplay("Black Player", blackTime);
        blackPlayerDisplay.setBorder(new EmptyBorder(10,10,10,10));
        mainPanel.add(blackPlayerDisplay);

        whitePlayerDisplay = new PlayerDisplay("White Player", whiteTime);
        whitePlayerDisplay.setBorder(new EmptyBorder(10,10,10,10));
        mainPanel.add(whitePlayerDisplay);
    }

    public void click(int row, int column) {
        if (isReplay) return;

        for (Move move : legalMoves) {
            if (move.toRow == row && move.toColumn == column) {
                selectedRow = -1;
                selectedColumn = -1;
                makeMove(move);
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

    public void makeMove(Move move) {
        ChessResult result = game.makeMove(move, false);

        if (result != null && result != ChessResult.NONE) {
            if (result == ChessResult.CHECK) {
                inCheck = true;
            } else {
                legalMoves.clear();
                refresh();
                endGame(result == ChessResult.CHECKMATE ? GameResult.CHECKMATE : GameResult.STALEMATE, !game.whiteTurn);
            }
        } else {
            inCheck = false;
        }

        if (game.whiteTurn) {
            blackTime += move_consider_time;
        } else {
            whiteTime += move_consider_time;
        }
    }

    public void refresh() {
        int min = Math.min(mainPanel.getWidth(), mainPanel.getHeight());
        int h = mainPanel.getHeight() / 8;
        int size = Math.min(min, mainPanel.getHeight() - h * 2);

        boardPanel.setBounds((mainPanel.getWidth()-size)/2,(mainPanel.getHeight()-size)/2,size,size);

        if (playingAsWhite) {
            blackPlayerDisplay.setBounds((mainPanel.getWidth() - size) / 2,0,size,h);
            whitePlayerDisplay.setBounds((mainPanel.getWidth() -size) / 2,mainPanel.getHeight() - h,size,h);
        } else {
            blackPlayerDisplay.setBounds((mainPanel.getWidth() - size) / 2,mainPanel.getHeight() - h,size,h);
            whitePlayerDisplay.setBounds((mainPanel.getWidth() - size) / 2,0,size,h);
        }

        blackPlayerDisplay.update(blackTime);
        whitePlayerDisplay.update(whiteTime);

        int cell = Math.min(boardPanel.getWidth(), boardPanel.getHeight()) / 8;

        for (int r=0;r<8;r++){
            for (int c=0;c<8;c++){
                JButton b = board[r][c];

                if (r==selectedRow && c==selectedColumn) {
                    b.setBackground(new Color(80,124,101));
                } else if ((r+c)%2==0) {
                    b.setBackground(new Color(222,227,230));
                    b.setBorder(new BevelBorder(BevelBorder.LOWERED));
                } else {
                    b.setBackground(new Color(140,162,173));
                    b.setBorder(new BevelBorder(BevelBorder.RAISED));
                }

                Piece p = game.board[r][c];

                if (p != null && inCheck && p.white == game.whiteTurn && p.type == Piece.Type.KING) {
                    b.setBackground(new Color(239,81,81));
                }

                if (p == null) {
                    b.setIcon(null);
                } else {
                    Image img = getPieceImage("/gui/assets/pieces/" + p.getSymbol() + ".png");
                    b.setIcon(new ImageIcon(img.getScaledInstance(cell, cell, Image.SCALE_SMOOTH)));
                }
            }
        }

        if (game.whiteTurn) {
            whitePlayerDisplay.setBorder(new LineBorder(Color.BLACK, 3));
            blackPlayerDisplay.setBorder(new EmptyBorder(3, 3,3, 3));
        } else {
            whitePlayerDisplay.setBorder(new EmptyBorder(3, 3, 3, 3));
            blackPlayerDisplay.setBorder(new LineBorder(Color.BLACK, 3));
        }

        highlight();
        repaint();
    }

    public void highlight() {
        for (Move m : legalMoves) {
            board[m.toRow][m.toColumn].setBackground(new Color(155,199,0));
        }
    }

    @Override
    public void doLayout() {
        super.doLayout();
        refresh();
    }

    public Image getPieceImage(String path) {
        if (!pieceImages.containsKey(path)) {
            pieceImages.put(path, new ImageIcon(Objects.requireNonNull(getClass().getResource(path))).getImage());
        }
        return pieceImages.get(path);
    }

    public void endGame(GameResult result, boolean whiteWins) {
        if (timer != null) timer.stop();

        if (game instanceof Game) {
            PGNWriter.saveMovesInDatabase((Game) game);
        }

        HostResultDialog.show(game, result, whiteWins);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            int w = getWidth();
            int h = getHeight();

            int iw = backgroundImage.getWidth(this);
            int ih = backgroundImage.getHeight(this);

            double s = Math.max((double)w/iw,(double)h/ih);

            int nw = (int)(iw*s);
            int nh = (int)(ih*s);

            int x = (w-nw)/2;
            int y = (h-nh)/2;

            g.drawImage(backgroundImage,x,y,nw,nh,null);
        }
    }
}