package edu.cs449.solitaire;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import edu.cs449.solitaire.SolitaireGame.Move;
import edu.cs449.solitaire.SolitaireGame.Position;

/** Acceptance coverage for the Sprint 3 manual, automated and replay features. */
class Sprint3AcceptanceTest {
  @TempDir
  Path tempDirectory;

  private Move jump(int row, int column, int targetRow, int targetColumn) {
    return new Move(new Position(row, column), new Position(targetRow, targetColumn));
  }

  @Test
  void manualModeAppliesOnlyThePlayersLegalMove() {
    ManualGameSession session = new ManualGameSession(new BoardShape.English(), 7, true);
    assertEquals(GameMode.MANUAL, session.mode());
    assertFalse(session.move(jump(1, 3, 1, 5)));
    assertEquals(0, session.game().moveCount());
    assertEquals(1, session.recording().orElseThrow().frames().size());

    assertTrue(session.move(jump(1, 3, 3, 3)));
    assertEquals(1, session.game().moveCount());
    assertEquals(31, session.game().pegCount());
    assertEquals(2, session.recording().orElseThrow().frames().size());
  }

  @Test
  void randomizationPreservesBoardShapePegCountAndMoveCount() {
    ManualGameSession session = new ManualGameSession(new BoardShape.Hexagon(), 7, true);
    assertTrue(session.move(jump(1, 3, 3, 3)));
    SolitaireGame.State before = session.game().state();
    int pegs = session.game().pegCount();
    int moves = session.game().moveCount();

    assertTrue(session.randomize(new Random(449)));
    assertEquals(pegs, session.game().pegCount());
    assertEquals(moves, session.game().moveCount());
    assertNotEquals(before, session.game().state());
    for (int row = 0; row < session.game().size(); row++) {
      for (int column = 0; column < session.game().size(); column++) {
        if (!session.game().isPlayable(row, column)) {
          assertFalse(session.game().hasPeg(row, column));
        }
      }
    }
    assertEquals("RANDOMIZE",
        session.recording().orElseThrow().frames().get(2).action());
  }

  @Test
  void automatedModeMakesMovesUntilTheGameIsOver() {
    AutomatedGameSession session = new AutomatedGameSession(
        new BoardShape.Diamond(), 9, true);
    int initialPegs = session.game().pegCount();

    var played = session.playToEnd(new Random(449));

    assertEquals(GameMode.AUTOMATED, session.mode());
    assertFalse(played.isEmpty());
    assertTrue(session.game().isOver());
    assertTrue(session.game().legalMoves().isEmpty());
    assertEquals(played.size(), session.game().moveCount());
    assertEquals(initialPegs - played.size(), session.game().pegCount());
    assertEquals(played.size() + 1,
        session.recording().orElseThrow().frames().size());
  }

  @Test
  void savedRecordingLoadsAndRestoresEveryFrame() throws IOException {
    ManualGameSession session = new ManualGameSession(new BoardShape.English(), 7, true);
    assertTrue(session.move(jump(1, 3, 3, 3)));
    assertTrue(session.randomize(new Random(449)));
    GameRecording original = session.recording().orElseThrow();
    Path file = tempDirectory.resolve("manual-game.txt");

    original.save(file);
    GameRecording loaded = GameRecording.load(file);

    assertEquals("English", loaded.shapeName());
    assertEquals(7, loaded.size());
    assertEquals(GameMode.MANUAL, loaded.mode());
    assertEquals(original.frames(), loaded.frames());
    for (int index = 0; index < loaded.frames().size(); index++) {
      assertEquals(loaded.frames().get(index).state(), loaded.gameAt(index).state());
    }
  }

  @Test
  void malformedRecordingIsRejected() throws IOException {
    Path file = tempDirectory.resolve("bad-recording.txt");
    Files.writeString(file, "not a game recording");
    assertThrows(IOException.class, () -> GameRecording.load(file));
  }
}
