package edu.cs449.solitaire;

import java.util.Objects;
import java.util.Optional;
import edu.cs449.solitaire.SolitaireGame.Move;

/**
 * Shared session behavior for manual and automated games. Subclasses decide how
 * the next move is selected, while this class owns the common rules and recording.
 */
public abstract class GameSession {
  private final SolitaireGame game;
  private final GameMode mode;
  private final GameRecording recording;

  protected GameSession(BoardShape shape, int size, GameMode mode, boolean recordGame) {
    game = new SolitaireGame(Objects.requireNonNull(shape), size);
    this.mode = Objects.requireNonNull(mode);
    recording = recordGame ? new GameRecording(shape, size, mode, game) : null;
  }

  public final SolitaireGame game() {
    return game;
  }

  public final GameMode mode() {
    return mode;
  }

  public final Optional<GameRecording> recording() {
    return Optional.ofNullable(recording);
  }

  protected final boolean applyMove(Move move, String action) {
    if (game.isOver() || !game.move(move)) {
      return false;
    }
    recordFrame(action);
    return true;
  }

  protected final void recordFrame(String action) {
    if (recording != null) {
      recording.addFrame(action, game);
    }
  }
}
