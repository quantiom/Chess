package me.quantiom.chess.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.quantiom.chess.ChessGame;
import me.quantiom.chess.render.Renderer;
import me.quantiom.chess.util.Color;
import me.quantiom.chess.util.TexturePiecesWrapper;
import me.quantiom.chess.util.Utils;
import me.quantiom.chess.util.Vec2i;

import java.util.List;
import java.util.Map;

import static me.quantiom.chess.game.PieceColor.*;
import static me.quantiom.chess.game.PieceType.*;
import static me.quantiom.chess.util.Constants.*;
import static org.lwjgl.glfw.GLFW.*;

public class Board {
    private PieceLogic pieceLogic; // 8x8 (64 pieces total)

    private ChessGame game;

    private TexturePiecesWrapper whitePieces;
    private TexturePiecesWrapper blackPieces;

    private Vec2i selectedPiece;

    private int oldMouseState = GLFW_RELEASE;

    public Board(ChessGame game) {
        this.pieceLogic = new PieceLogic(new Piece[8][8]);
        this.game = game;
        this.resetPieces();
    }

    public void update() {
        Vec2i mousePos = Utils.getMousePos(this.game.getWindow());

        int x = (int) Math.ceil(mousePos.x / (double) SQUARE_SIZE) - 1;
        int y = (int) Math.ceil(mousePos.y / (double) SQUARE_SIZE) - 1;

        int newMouseState = glfwGetMouseButton(this.game.getWindow(), GLFW_MOUSE_BUTTON_LEFT);

        if (newMouseState == GLFW_RELEASE && oldMouseState == GLFW_PRESS) {
            if (x >= 0 && y >= 0) {
                if (this.selectedPiece != null) {
                    if (this.getPossibleMoves(this.selectedPiece.x, this.selectedPiece.y, false).stream().anyMatch(val -> val.x == x && val.y == y)) {
                        this.movePiece(new Vec2i(this.selectedPiece.x, this.selectedPiece.y), new Vec2i(x, y));
                        this.selectedPiece = null;
                    } else {
                        if (this.getPiece(x, y).getType() != NONE) {
                            this.selectedPiece = new Vec2i(x, y);
                        }
                    }
                } else {
                    if (this.getPiece(x, y).getType() != NONE) {
                        this.selectedPiece = new Vec2i(x, y);
                    }
                }
            } else {
                this.selectedPiece = null;
            }
        }

        if (glfwGetMouseButton(this.game.getWindow(), GLFW_MOUSE_BUTTON_RIGHT) == GLFW_PRESS) {
            this.selectedPiece = null;
        }

        oldMouseState = newMouseState;
    }

    public void draw() {
        this.drawSquares();

        if (this.selectedPiece != null) {
            Renderer.rectangle(this.selectedPiece.x * SQUARE_SIZE, this.selectedPiece.y * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE, 224, 245, 66, 135);
        }

        if (this.pieceLogic.isInCheck(WHITE) || this.pieceLogic.isInCheck(BLACK)) {
            Renderer.rectangle(this.pieceLogic.getCheckedKing().x * SQUARE_SIZE, this.pieceLogic.getCheckedKing().y * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE, 255, 0, 0, 100);
        }

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece piece = this.getPiece(x, y);

                if (piece.getType() == NONE) continue;

                (piece.getColor() == WHITE ? this.whitePieces : this.blackPieces).getFromType(piece.getType()).draw(x * SQUARE_SIZE, y * SQUARE_SIZE);
            }
        }

        if (this.selectedPiece != null) {
            this.getPossibleMoves(this.selectedPiece.x, this.selectedPiece.y, false).forEach(possibleMove -> {
                this.drawPossibleMoveDot(possibleMove.x, possibleMove.y);
            });
        }
    }

    private void drawPossibleMoveDot(int x, int y) {
        Renderer.circle(x * SQUARE_SIZE + (SQUARE_SIZE / 2), y * SQUARE_SIZE + (SQUARE_SIZE / 2), 7, new Color(0, 0, 0, 65));
    }

    // called once in context
    public void init() {
        this.whitePieces = new TexturePiecesWrapper("wP.png", "wN.png", "wB.png", "wR.png", "wQ.png", "wK.png");
        this.blackPieces = new TexturePiecesWrapper("bP.png", "bN.png", "bB.png", "bR.png", "bQ.png", "bK.png");
    }

    private void resetPieces() {
        // fill board
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                this.setPiece(x, y, NONE, NO_COLOR);
            }
        }

        // reset pieces
        this.resetPieces(WHITE);
        this.resetPieces(BLACK);
    }

    private void setPiece(int x, int y, PieceType type, PieceColor color) {
        //this.pieces[x][y] = new Piece(color, type);
        this.pieceLogic.setPiece(x, y, type, color);
    }

    @SuppressWarnings("Duplicates")
    private void resetPieces(PieceColor color) {
        // base y
        final int y = color == WHITE ? 7 : 0;

        // pawns
        for (int i = 0; i < 8; i++)
            this.setPiece(i, y + (color == WHITE ? -1 : 1), PAWN, color);

        // other
        this.setPiece(0, y, ROOK, color);
        this.setPiece(1, y, KNIGHT, color);
        this.setPiece(2, y, BISHOP, color);
        this.setPiece(3, y, QUEEN, color);
        this.setPiece(4, y, KING, color);
        this.setPiece(5, y, BISHOP, color);
        this.setPiece(6, y, KNIGHT, color);
        this.setPiece(7, y, ROOK, color);
    }

    private void drawSquares() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Renderer.rectangle(
                        x * SQUARE_SIZE,
                        y * SQUARE_SIZE,
                        SQUARE_SIZE,
                        SQUARE_SIZE,
                        (x + y % 2) % 2 == 1 ? BOARD_COLOR_ONE : BOARD_COLOR_TWO
                );
            }
        }
    }

    private List<Vec2i> getPossibleMoves(int x, int y, boolean checkedOnce) {
        return this.pieceLogic.getPossibleMoves(x, y, checkedOnce);
    }

    private void movePiece(Vec2i from, Vec2i to) {
        this.pieceLogic.movePiece(from, to, false);
    }

    private Piece getPiece(int x, int y) {
        return this.pieceLogic.getPiece(x, y);
    }

    private boolean isInCheck(PieceColor color) {
        return this.pieceLogic.isInCheck(color);
    }

    private Vec2i getKingPosition(PieceColor color) {
        return this.pieceLogic.getKingPosition(color);
    }

    private Map<Vec2i, Piece> getFlattenedPieces(PieceColor color) {
        return this.pieceLogic.getFlattenedPieces(color);
    }


}
