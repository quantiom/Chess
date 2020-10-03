package me.quantiom.chess.game;

public enum PieceColor {
    NO_COLOR,
    WHITE,
    BLACK;

    public static PieceColor getOpposite(PieceColor color) {
        if (color == WHITE)
            return BLACK;

        if (color == BLACK)
            return WHITE;

        return NO_COLOR;
    }
}
