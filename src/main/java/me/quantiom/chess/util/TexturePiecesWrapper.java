package me.quantiom.chess.util;

import me.quantiom.chess.game.PieceType;
import me.quantiom.chess.render.Texture;
import me.quantiom.chess.render.TextureUtil;

public class TexturePiecesWrapper {
    private Texture pawn,
            knight,
            bishop,
            rook,
            queen,
            king;

    public TexturePiecesWrapper(String pawn, String knight, String bishop, String rook, String queen, String king) {
        this.pawn = TextureUtil.createTexture("assets/pieces/" + pawn);
        this.knight = TextureUtil.createTexture("assets/pieces/" + knight);
        this.bishop = TextureUtil.createTexture("assets/pieces/" + bishop);
        this.rook = TextureUtil.createTexture("assets/pieces/" + rook);
        this.queen = TextureUtil.createTexture("assets/pieces/" + queen);
        this.king = TextureUtil.createTexture("assets/pieces/" + king);
    }

    public Texture getFromType(PieceType type) {
        switch (type) {
            case PAWN:
                return this.pawn;
            case KNIGHT:
                return this.knight;
            case BISHOP:
                return this.bishop;
            case ROOK:
                return this.rook;
            case QUEEN:
                return this.queen;
            case KING:
                return this.king;
            default:
                return this.pawn;
        }
    }

    public Texture getPawn() {
        return pawn;
    }

    public Texture getKnight() {
        return knight;
    }

    public Texture getBishop() {
        return bishop;
    }

    public Texture getRook() {
        return rook;
    }

    public Texture getQueen() {
        return queen;
    }

    public Texture getKing() {
        return king;
    }
}
