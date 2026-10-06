package edu.cs449.solitaire;

/** Converts the number of pegs remaining into a player rating. */
public final class PlayerRating {
  /** Returns a rating for a positive peg count; rejects zero and negative counts. */
  public String rate(int pegsRemaining) {
    if (pegsRemaining < 1) {
      throw new IllegalArgumentException("At least one peg must remain.");
    }
    return switch (pegsRemaining) {
      case 1 -> "Outstanding";
      case 2 -> "Very Good";
      case 3 -> "Good";
      default -> "Average";
    };
  }
}
