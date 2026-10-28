package edu.cs449.solitaire;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Manual solitaire rules. Coordinates use rows and columns for every board type. */
public final class SolitaireGame {
  public record Position(int row, int column) {}

  public record Move(Position from, Position to) {}

  private final BoardShape shape;
  private final int size;
  private final boolean[][] pegs;
  private int moveCount;

  public SolitaireGame(BoardShape shape, int size) {
    this.shape = Objects.requireNonNull(shape);
    if (size != 7 && size != 9) {
      throw new IllegalArgumentException("Choose board size 7 or 9.");
    }
    this.size = size;
    pegs = new boolean[size][size];
    for (int row = 0; row < size; row++) {
      for (int column = 0; column < size; column++) {
        pegs[row][column] = isPlayable(row, column);
      }
    }
    pegs[size / 2][size / 2] = false;
  }

  public int size() {
    return size;
  }

  public BoardShape shape() {
    return shape;
  }

  public int moveCount() {
    return moveCount;
  }

  public boolean isPlayable(int row, int column) {
    return row >= 0 && row < size && column >= 0 && column < size
        && shape.contains(row, column, size);
  }

  public boolean hasPeg(int row, int column) {
    return isPlayable(row, column) && pegs[row][column];
  }

  public int pegCount() {
    int count = 0;
    for (boolean[] row : pegs) {
      for (boolean peg : row) {
        if (peg) {
          count++;
        }
      }
    }
    return count;
  }

  public boolean isLegal(Move move) {
    if (move == null || move.from() == null || move.to() == null) {
      return false;
    }
    Position from = move.from();
    Position to = move.to();
    if (!hasPeg(from.row(), from.column()) || !isPlayable(to.row(), to.column())
        || hasPeg(to.row(), to.column())) {
      return false;
    }
    int dr = to.row() - from.row();
    int dc = to.column() - from.column();
    boolean straightJump = (Math.abs(dr) == 2 && dc == 0)
        || (dr == 0 && Math.abs(dc) == 2)
        || (Math.abs(dr) == 2 && Math.abs(dc) == 2);
    return straightJump && hasPeg(from.row() + dr / 2, from.column() + dc / 2);
  }

  /** Invalid moves leave the entire game unchanged. */
  public boolean move(Move move) {
    if (!isLegal(move)) {
      return false;
    }
    Position from = move.from();
    Position to = move.to();
    pegs[from.row()][from.column()] = false;
    pegs[(from.row() + to.row()) / 2][(from.column() + to.column()) / 2] = false;
    pegs[to.row()][to.column()] = true;
    moveCount++;
    return true;
  }

  public List<Move> legalMoves() {
    List<Move> moves = new ArrayList<>();
    for (int row = 0; row < size; row++) {
      for (int column = 0; column < size; column++) {
        for (int dr = -2; dr <= 2; dr += 2) {
          for (int dc = -2; dc <= 2; dc += 2) {
            Move move = new Move(new Position(row, column),
                new Position(row + dr, column + dc));
            if (isLegal(move)) {
              moves.add(move);
            }
          }
        }
      }
    }
    return List.copyOf(moves);
  }

  public boolean isOver() {
    return pegCount() == 1 || legalMoves().isEmpty();
  }

  public String result() {
    return isOver() ? new PlayerRating().rate(pegCount()) : "";
  }
}
