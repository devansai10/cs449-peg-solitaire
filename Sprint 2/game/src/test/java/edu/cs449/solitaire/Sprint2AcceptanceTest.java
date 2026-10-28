package edu.cs449.solitaire;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import edu.cs449.solitaire.SolitaireGame.Move;
import edu.cs449.solitaire.SolitaireGame.Position;

/** Sprint 2 acceptance coverage. The first two tests were drafted with ChatGPT. */
class Sprint2AcceptanceTest {

  /** AI-assisted test 1 - AC 1.2, 2.1 and 2.2: selected settings create a fresh game. */
  @Test
  void restartingWithSelectedBoardResetsMovesAndCenter() {
    SolitaireGame previous = new SolitaireGame(new BoardShape.English(), 7);
    assertTrue(previous.move(new Move(new Position(1, 3), new Position(3, 3))));
    assertEquals(1, previous.moveCount());
    assertEquals(31, previous.pegCount());

    // Mirrors the New Game button creating a new model with the selected settings.
    SolitaireGame restarted = new SolitaireGame(new BoardShape.Diamond(), 9);
    assertEquals(9, restarted.size());
    assertEquals("Diamond", restarted.shape().toString());
    assertEquals(40, restarted.pegCount());
    assertEquals(0, restarted.moveCount());
    assertFalse(restarted.hasPeg(4, 4));
    assertFalse(restarted.isOver());
    assertEquals("", restarted.result());

    // Changing the future settings does not mutate the previous game instance.
    assertEquals(1, previous.moveCount());
    assertEquals(31, previous.pegCount());
  }

  /** AI-assisted test 2 - AC 3.2: an invalid jump never mutates state. */
  @Test
  void invalidJumpLeavesEveryCellUnchangedAndAllowsRecovery() {
    SolitaireGame game = new SolitaireGame(new BoardShape.English(), 7);
    boolean[][] before = new boolean[7][7];
    for (int r = 0; r < 7; r++) {
      for (int c = 0; c < 7; c++) {
        before[r][c] = game.hasPeg(r, c);
      }
    }
    int initialMoves = game.moveCount();
    int initialPegs = game.pegCount();
    // Source and target both occupied: invalid, so board must be identical.
    assertFalse(game.move(new Move(new Position(1, 3), new Position(1, 5))));
    assertEquals(initialMoves, game.moveCount());
    assertEquals(initialPegs, game.pegCount());
    for (int r = 0; r < 7; r++) {
      for (int c = 0; c < 7; c++) {
        assertEquals(before[r][c], game.hasPeg(r, c));
      }
    }
    // A valid move after a rejected move must still work.
    assertTrue(game.move(new Move(new Position(1, 3), new Position(3, 3))));
    assertEquals(initialMoves + 1, game.moveCount());
    assertEquals(initialPegs - 1, game.pegCount());
  }

  /** AC 4.2 and 4.3: play continues while possible and ends when no moves remain. */
  @Test
  void deterministicGameEndsAndRejectsFurtherMoves() {
    SolitaireGame game = new SolitaireGame(new BoardShape.English(), 7);
    while (!game.isOver()) {
      assertFalse(game.legalMoves().isEmpty());
      assertTrue(game.move(game.legalMoves().get(0)));
    }
    assertTrue(game.isOver());
    assertTrue(game.legalMoves().isEmpty());
    assertEquals(new PlayerRating().rate(game.pegCount()), game.result());
    int pegs = game.pegCount();
    int moves = game.moveCount();
    assertFalse(game.move(new Move(new Position(1, 3), new Position(3, 3))));
    assertEquals(pegs, game.pegCount());
    assertEquals(moves, game.moveCount());
  }

  /** AC 4.1: a legal final jump leaves one peg and produces Outstanding. */
  @Test
  void onePegFinishIsOutstanding() {
    SolitaireGame game = new SolitaireGame(new BoardShape.English(), 7);
    int[][] moves = {
        {1, 3, 3, 3}, {2, 1, 2, 3}, {0, 2, 2, 2}, {3, 1, 1, 3},
        {0, 4, 2, 2}, {3, 3, 1, 3}, {3, 5, 3, 3}, {0, 3, 2, 3},
        {1, 4, 3, 4}, {2, 2, 2, 4}, {2, 5, 2, 3}, {3, 3, 1, 3},
        {5, 3, 3, 3}, {4, 1, 2, 3}, {4, 5, 4, 3}, {5, 2, 3, 2},
        {6, 4, 4, 4}, {3, 4, 5, 4}, {6, 3, 4, 5}, {4, 6, 4, 4},
        {4, 3, 4, 5}, {2, 6, 4, 6}, {4, 6, 4, 4}, {4, 4, 2, 2},
        {2, 3, 2, 1}, {2, 0, 2, 2}, {1, 3, 3, 1}, {4, 0, 2, 0},
        {2, 0, 4, 2}, {3, 2, 5, 2}, {6, 2, 4, 2}
    };

    for (int[] move : moves) {
      assertTrue(game.move(new Move(
          new Position(move[0], move[1]), new Position(move[2], move[3]))));
    }

    assertEquals(1, game.pegCount());
    assertEquals(31, game.moveCount());
    assertTrue(game.isOver());
    assertTrue(game.legalMoves().isEmpty());
    assertEquals("Outstanding", game.result());
  }
}
