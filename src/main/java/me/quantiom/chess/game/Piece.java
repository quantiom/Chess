package me.quantiom.chess.game;

import java.io.Serializable;

import static me.quantiom.chess.game.PieceColor.NO_COLOR;
import static me.quantiom.chess.game.PieceType.NONE;

public class Piece implements Serializable {
    private PieceColor color;
    private PieceType type;

    public Piece(PieceColor color, PieceType type) {
        this.color = color;
        this.type = type;
    }

    public PieceColor getColor() {
        return color;
    }

    public PieceType getType() {
        return type;
    }

    public static Piece getEmpty() {
        return new Piece(NO_COLOR, NONE);
    }
}
