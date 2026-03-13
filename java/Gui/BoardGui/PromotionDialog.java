package Gui.BoardGui;

import javax.swing.*;
import java.util.Objects;

public class PromotionDialog {

    public static Piece.Type choosePromotion(boolean white) {
        String[] options = {
                "Queen",
                "Rook",
                "Bishop",
                "Knight"
        };
        int res = JOptionPane.showOptionDialog(
                null,
                "Choose promotion piece",
                "Pawn Promotion",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                new ImageIcon(Objects.requireNonNull(PromotionDialog.class.getResource("images/pieces/" + (white ? "white" : "black") + "/pawn.png"))),
                options,
                options[0]
        );
        return switch (res) {
            case 1 -> Piece.Type.ROOK;
            case 2 -> Piece.Type.BISHOP;
            case 3 -> Piece.Type.KNIGHT;
            default -> Piece.Type.QUEEN;
        };
    }
}