package edu.cs449.solitaire;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import edu.cs449.solitaire.SolitaireGame.Move;

/** The computer chooses a legal move until no move remains. */
public final class AutomatedGameSession extends GameSession {
  public AutomatedGameSession(BoardShape shape, int size, boolean recordGame) {
    super(shape, size, GameMode.AUTOMATED, recordGame);
  }

  public Optional<Move> moveNext(Random random) {
    Objects.requireNonNull(random);
    List<Move> moves = game().legalMoves();
    if (moves.isEmpty()) {
      return Optional.empty();
    }
    Move selected = moves.get(random.nextInt(moves.size()));
    String action = "AUTO_MOVE " + selected.from().row() + "," + selected.from().column()
        + " TO " + selected.to().row() + "," + selected.to().column();
    if (!applyMove(selected, action)) {
      throw new IllegalStateException("A move selected from legalMoves must be legal.");
    }
    return Optional.of(selected);
  }

  public List<Move> playToEnd(Random random) {
    List<Move> played = new ArrayList<>();
    Optional<Move> move;
    while ((move = moveNext(random)).isPresent()) {
      played.add(move.get());
    }
    return List.copyOf(played);
  }
}
