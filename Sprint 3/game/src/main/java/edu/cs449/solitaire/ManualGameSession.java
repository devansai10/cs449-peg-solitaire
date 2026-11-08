package edu.cs449.solitaire;

import java.util.Objects;
import java.util.Random;
import edu.cs449.solitaire.SolitaireGame.Move;

/** A human chooses each move and may randomize the board during play. */
public final class ManualGameSession extends GameSession {
  public ManualGameSession(BoardShape shape, int size, boolean recordGame) {
    super(shape, size, GameMode.MANUAL, recordGame);
  }

  public boolean move(Move move) {
    String action = move == null || move.from() == null || move.to() == null ? "MOVE"
        : "MOVE " + move.from().row() + "," + move.from().column() + " TO "
            + move.to().row() + "," + move.to().column();
    return applyMove(move, action);
  }

  public boolean randomize(Random random) {
    Objects.requireNonNull(random);
    if (game().isOver()) {
      return false;
    }
    game().randomize(random);
    recordFrame("RANDOMIZE");
    return true;
  }
}
