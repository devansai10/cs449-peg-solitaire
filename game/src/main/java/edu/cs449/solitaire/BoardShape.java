package edu.cs449.solitaire;

/** Defines playable positions independently of the game and its interface. */
public abstract class BoardShape {
  public abstract boolean contains(int row, int column, int size);

  public static final class English extends BoardShape {
    @Override
    public boolean contains(int row, int column, int size) {
      int middle = size / 2;
      return Math.abs(row - middle) <= 1 || Math.abs(column - middle) <= 1;
    }

    @Override
    public String toString() {
      return "English";
    }
  }

  public static final class Diamond extends BoardShape {
    @Override
    public boolean contains(int row, int column, int size) {
      int middle = size / 2;
      return Math.abs(row - middle) + Math.abs(column - middle) <= middle;
    }

    @Override
    public String toString() {
      return "Diamond";
    }
  }

  /** Axial hexagonal mask; the interface offsets rows to show its six sides. */
  public static final class Hexagon extends BoardShape {
    @Override
    public boolean contains(int row, int column, int size) {
      return Math.abs(row + column - (size - 1)) <= size / 2;
    }

    @Override
    public String toString() {
      return "Hexagon";
    }
  }
}
