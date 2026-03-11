import javax.swing.*;
import java.awt.*;

public class BoardGui extends JPanel {

    JPanel boardPanel;
    JButton[][] board = new JButton[8][8];

    public BoardGui() {

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
    }

    public void click(int row, int column) {

    }
}
