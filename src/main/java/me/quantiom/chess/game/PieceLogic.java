package me.quantiom.chess.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.quantiom.chess.util.Vec2i;
import org.apache.commons.lang3.SerializationUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static me.quantiom.chess.game.PieceColor.*;
import static me.quantiom.chess.game.PieceType.*;

public class PieceLogic {
    private Piece[][] pieces;

    private Vec2i checkedKing;

    public PieceLogic(Piece[][] pieces) {
        this.pieces = pieces;
    }

    public List<Vec2i> getPossibleMoves(int x, int y, boolean checkedOnce) {
        List<Vec2i> possibleMoves = Lists.newArrayList();

        Piece piece = this.getPiece(x, y);
        if (piece.getType() == NONE) return possibleMoves;

        final boolean reverse = piece.getColor() == WHITE;

        if (piece.getType() == PAWN) {
            Vec2i upCoords = new Vec2i(x, y + (reverse ? -1 : 1));

            if (upCoords.y <= 7 && upCoords.y >= 0) {
                // up 1
                if (this.getPiece(upCoords.x, upCoords.y) != null
                        && this.getPiece(upCoords.x, upCoords.y).getType() == NONE) {
                    possibleMoves.add(new Vec2i(upCoords.x, upCoords.y));
                }

                // up 2
                if (this.getPiece(upCoords.x, upCoords.y) != null && this.getPiece(upCoords.x, upCoords.y).getType() == NONE && // if there is no piece 1 above
                        this.getPiece(upCoords.x, upCoords.y + (reverse ? -1 : 1)) != null && // if the piece above 2 isn't null
                        this.getPiece(upCoords.x, upCoords.y + (reverse ? -1 : 1)).getType() == NONE && // if there is no piece up 2
                        y == (piece.getColor() == WHITE ? 6 : 1)) // if it hasn't moved
                    possibleMoves.add(new Vec2i(upCoords.x, upCoords.y + (reverse ? -1 : 1)));

                // up right
                if (this.getPiece(upCoords.x + 1, upCoords.y) != null &&
                        this.getPiece(upCoords.x + 1, upCoords.y).getType() != NONE &&
                        this.getPiece(upCoords.x + 1, upCoords.y).getColor() != piece.getColor())
                    possibleMoves.add(new Vec2i(upCoords.x + 1, upCoords.y));

                // up left
                if (this.getPiece(upCoords.x - 1, upCoords.y) != null &&
                        this.getPiece(upCoords.x - 1, upCoords.y).getType() != NONE &&
                        this.getPiece(upCoords.x - 1, upCoords.y).getColor() != piece.getColor())
                    possibleMoves.add(new Vec2i(upCoords.x - 1, upCoords.y));
            }
        } else if (piece.getType() == KNIGHT) {
            // up and down
            for (int j = 0; j < 2; j++) {
                Vec2i baseLocation = new Vec2i(x, y + (j == 1 ? -2 : 2));

                for (int i = 0; i < 2; i++) {
                    final int offset = i == 1 ? -1 : 1;
                    Piece targetPiece = this.getPiece(baseLocation.x + offset, baseLocation.y);

                    if (targetPiece != null) {
                        if (targetPiece.getType() != NONE && targetPiece.getColor() == piece.getColor()) continue;
                        possibleMoves.add(new Vec2i(baseLocation.x + offset, baseLocation.y));
                    }
                }
            }

            // left and right
            for (int j = 0; j < 2; j++) {
                Vec2i baseLocation = new Vec2i(x + (j == 1 ? -2 : 2), y);

                for (int i = 0; i < 2; i++) {
                    final int offset = i == 1 ? -1 : 1;
                    Piece targetPiece = this.getPiece(baseLocation.x, baseLocation.y + offset);

                    if (targetPiece != null) {
                        if (targetPiece.getType() != NONE && targetPiece.getColor() == piece.getColor()) continue;
                        possibleMoves.add(new Vec2i(baseLocation.x, baseLocation.y + offset));
                    }
                }
            }
        } else if (piece.getType() == KING) {
            List<Vec2i> offsets = Lists.newArrayList(
                    new Vec2i(-1, 1),
                    new Vec2i(0, 1),
                    new Vec2i(1, 1),
                    new Vec2i(-1, 0),
                    new Vec2i(0, 0),
                    new Vec2i(1, 0),
                    new Vec2i(-1, -1),
                    new Vec2i(0, -1),
                    new Vec2i(1, -1)
            );

            offsets.forEach(offset -> {
                Piece targetPiece = this.getPiece(x + offset.x, y + offset.y);
                if (targetPiece == null) return;

                if (targetPiece.getType() == NONE || (targetPiece.getType() != NONE && targetPiece.getColor() != piece.getColor())) {
                    possibleMoves.add(new Vec2i(x + offset.x, y + offset.y));
                }
            });
        }

        if (piece.getType() == BISHOP || piece.getType() == QUEEN) {
            // up-left
            for (int i = 1; i < 8; i++) {
                Piece checkPiece = this.getPiece(x - i, y + i * (reverse ? -1 : 1));

                if (checkPiece == null) break;

                if (checkPiece.getType() != NONE) {
                    if (checkPiece.getColor() != piece.getColor()) possibleMoves.add(new Vec2i(x - i, y + i * (reverse ? -1 : 1)));
                    break;
                }

                possibleMoves.add(new Vec2i(x - i, y + i * (reverse ? -1 : 1)));
            }

            // up-right
            for (int i = 1; i < 8; i++) {
                Piece checkPiece = this.getPiece(x + i, y + i * (reverse ? -1 : 1));

                if (checkPiece == null) break;

                if (checkPiece.getType() != NONE) {
                    if (checkPiece.getColor() != piece.getColor()) possibleMoves.add(new Vec2i(x + i, y + i * (reverse ? -1 : 1)));
                    break;
                }

                possibleMoves.add(new Vec2i(x + i, y + i * (reverse ? -1 : 1)));
            }

            // down-right
            for (int i = 1; i < 8; i++) {
                Piece checkPiece = this.getPiece(x + i, y - i * (reverse ? -1 : 1));

                if (checkPiece == null) break;

                if (checkPiece.getType() != NONE) {
                    if (checkPiece.getColor() != piece.getColor()) possibleMoves.add(new Vec2i(x + i, y - i * (reverse ? -1 : 1)));
                    break;
                }

                possibleMoves.add(new Vec2i(x + i, y - i * (reverse ? -1 : 1)));
            }

            // down-left
            for (int i = 1; i < 8; i++) {
                Piece checkPiece = this.getPiece(x - i, y - i * (reverse ? -1 : 1));

                if (checkPiece == null) break;

                if (checkPiece.getType() != NONE) {
                    if (checkPiece.getColor() != piece.getColor()) possibleMoves.add(new Vec2i(x - i, y - i * (reverse ? -1 : 1)));
                    break;
                }

                possibleMoves.add(new Vec2i(x - i, y - i * (reverse ? -1 : 1)));
            }
        }

        if (piece.getType() == ROOK || piece.getType() == QUEEN) {
            // right
            for (int i = 1; i < 8; i++) {
                if (this.getPiece(x + i, y) == null) break;

                if (this.getPiece(x + i, y) != null && this.getPiece(x + i, y).getType() != NONE) {
                    if (this.getPiece(x + i, y).getColor() != piece.getColor()) possibleMoves.add(new Vec2i(x + i, y));
                    break;
                }

                possibleMoves.add(new Vec2i(x + i, y));
            }

            // left
            for (int i = 1; i < 8; i++) {
                if (this.getPiece(x - i, y) == null) break;

                if (this.getPiece(x - i, y) != null && this.getPiece(x - i, y).getType() != NONE) {
                    if (this.getPiece(x - i, y).getColor() != piece.getColor()) possibleMoves.add(new Vec2i(x - i, y));
                    break;
                }

                possibleMoves.add(new Vec2i(x - i, y));
            }

            // up
            for (int i = 1; i < 8; i++) {
                if (this.getPiece(x, y - i) == null) break;

                if (this.getPiece(x, y - i) != null && this.getPiece(x, y - i).getType() != NONE) {
                    if (this.getPiece(x, y - i).getColor() != piece.getColor()) possibleMoves.add(new Vec2i(x, y - i));
                    break;
                }

                possibleMoves.add(new Vec2i(x, y - i));
            }

            // down
            for (int i = 1; i < 8; i++) {
                if (this.getPiece(x, y + i) == null) break;

                if (this.getPiece(x, y + i) != null && this.getPiece(x, y + i).getType() != NONE) {
                    if (this.getPiece(x, y + i).getColor() != piece.getColor()) possibleMoves.add(new Vec2i(x, y + i));
                    break;
                }

                possibleMoves.add(new Vec2i(x, y + i));
            }
        }

        if (!checkedOnce) {// && this.isInCheck(piece.getColor())) {
            return possibleMoves.stream().filter(move -> {
                PieceLogic simulated = new PieceLogic(SerializationUtils.clone(this.pieces));
                simulated.movePiece(new Vec2i(x, y), new Vec2i(move.x, move.y), true);

                return !simulated.isInCheck(piece.getColor());
            }).collect(Collectors.toList());
        }

        return possibleMoves;
    }

    public Piece getPiece(int x, int y) {
        if (x > 7 || x < 0 || y > 7 || y < 0) return null;

        return this.pieces[x][y];
    }

    public boolean isInCheck(PieceColor color) {
        Map<Vec2i, Piece> flattenedPieces = this.getFlattenedPieces(PieceColor.getOpposite(color));
        Vec2i kingPosition = this.getKingPosition(color);

        return flattenedPieces.keySet().stream().anyMatch(vec -> this.getPossibleMoves(vec.x, vec.y, true).stream().anyMatch(v -> v.x == kingPosition.x && v.y == kingPosition.y));
    }

    public Vec2i getKingPosition(PieceColor color) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece piece = this.getPiece(x, y);
                if (piece.getColor() == color && piece.getType() == KING) return new Vec2i(x, y);
            }
        }

        return new Vec2i(0, 0);
    }

    public Map<Vec2i, Piece> getFlattenedPieces(PieceColor color) {
        Map<Vec2i, Piece> pieces = Maps.newHashMap();

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece piece = this.getPiece(x, y);
                if (piece.getColor() == color) pieces.put(new Vec2i(x, y), piece);
            }
        }

        return pieces;
    }

    public void movePiece(Vec2i from, Vec2i to, boolean hasChecked) {
        Piece old = this.getPiece(from.x, from.y);
        this.setPiece(from.x, from.y, NONE, NO_COLOR);
        this.pieces[to.x][to.y] = old;

        if (this.isInCheck(WHITE)) {
            this.checkedKing = this.getKingPosition(WHITE);

            if (!hasChecked && this.getFlattenedPieces(WHITE).keySet().stream().noneMatch(vec -> !this.getPossibleMoves(vec.x, vec.y, false).isEmpty())) {
                System.out.println("White is checkmated!");
            }
        } else if (this.isInCheck(BLACK)) {
            if (!hasChecked && this.getFlattenedPieces(BLACK).keySet().stream().noneMatch(vec -> !this.getPossibleMoves(vec.x, vec.y, false).isEmpty())) {
                System.out.println("Black is checkmated!");
            }

            this.checkedKing = this.getKingPosition(BLACK);
        }
    }

    public void setPiece(int x, int y, PieceType type, PieceColor color) {
        this.pieces[x][y] = new Piece(color, type);
    }

    public Piece[][] getPieces() {
        return pieces;
    }

    public Vec2i getCheckedKing() {
        return checkedKing;
    }
}
