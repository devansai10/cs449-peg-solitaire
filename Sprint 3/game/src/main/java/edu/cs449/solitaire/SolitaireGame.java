package edu.cs449.solitaire;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/** Manual solitaire rules. Coordinates use rows and columns for every board type. */
public final class SolitaireGame {
  public record Position(int row, int column) {}

  public record Move(Position from, Position to) {}

  /** Immutable board snapshot used by recording and replay. */
  public record State(List<String> rows, int moveCount) {
    public State {
      rows = List.copyOf(rows);
      if (moveCount < 0) {
        throw new IllegalArgumentException("Move count cannot be negative.");
      }
    }
  }

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

  /**
   * Rearranges the current pegs over playable holes without changing their count.
   * Randomization is a board-state operation, so it does not increase moveCount.
   */
  public void randomize(Random random) {
    Objects.requireNonNull(random);
    int count = pegCount();
    List<Position> positions = playablePositions();
    Collections.shuffle(positions, random);
    for (int row = 0; row < size; row++) {
      for (int column = 0; column < size; column++) {
        if (isPlayable(row, column)) {
          pegs[row][column] = false;
        }
      }
    }
    for (int index = 0; index < count; index++) {
      Position position = positions.get(index);
      pegs[position.row()][position.column()] = true;
    }
  }

  /** Returns a text-friendly immutable copy of the complete game state. */
  public State state() {
    List<String> rows = new ArrayList<>();
    for (int row = 0; row < size; row++) {
      StringBuilder encoded = new StringBuilder(size);
      for (int column = 0; column < size; column++) {
        if (!isPlayable(row, column)) {
          encoded.append('-');
        } else {
          encoded.append(pegs[row][column] ? '1' : '0');
        }
      }
      rows.add(encoded.toString());
    }
    return new State(rows, moveCount);
  }

  /** Restores a validated snapshot. This is used only by the replay feature. */
  public void restore(State state) {
    Objects.requireNonNull(state);
    if (state.rows().size() != size) {
      throw new IllegalArgumentException("Recorded state has the wrong board height.");
    }
    int restoredPegs = 0;
    for (int row = 0; row < size; row++) {
      String encoded = state.rows().get(row);
      if (encoded.length() != size) {
        throw new IllegalArgumentException("Recorded state has the wrong board width.");
      }
      for (int column = 0; column < size; column++) {
        char value = encoded.charAt(column);
        if (!isPlayable(row, column)) {
          if (value != '-') {
            throw new IllegalArgumentException("Recorded state uses a non-playable hole.");
          }
          pegs[row][column] = false;
        } else if (value == '0' || value == '1') {
          pegs[row][column] = value == '1';
          if (pegs[row][column]) {
            restoredPegs++;
          }
        } else {
          throw new IllegalArgumentException("Recorded state contains an invalid character.");
        }
      }
    }
    if (restoredPegs < 1) {
      throw new IllegalArgumentException("A recorded state must contain at least one peg.");
    }
    moveCount = state.moveCount();
  }

  private List<Position> playablePositions() {
    List<Position> positions = new ArrayList<>();
    for (int row = 0; row < size; row++) {
      for (int column = 0; column < size; column++) {
        if (isPlayable(row, column)) {
          positions.add(new Position(row, column));
        }
      }
    }
    return positions;
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
