package edu.cs449.solitaire;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import edu.cs449.solitaire.SolitaireGame.Move;
import edu.cs449.solitaire.SolitaireGame.Position;

class SolitaireGameTest {
  private Move jump(int row, int column, int targetRow, int targetColumn) {
    return new Move(new Position(row, column), new Position(targetRow, targetColumn));
  }

  @Test
  void everyLayoutStartsWithOnlyItsCenterEmpty() {
    BoardShape[] shapes = {new BoardShape.English(), new BoardShape.Hexagon(),
        new BoardShape.Diamond()};
    int[][] expectedCounts = {{32, 44}, {36, 60}, {24, 40}};
    for (int type = 0; type < shapes.length; type++) {
      for (int index = 0; index < 2; index++) {
        int size = index == 0 ? 7 : 9;
        SolitaireGame game = new SolitaireGame(shapes[type], size);
        assertEquals(expectedCounts[type][index], game.pegCount());
        assertFalse(game.hasPeg(size / 2, size / 2));
        assertFalse(game.isOver());
        assertEquals("", game.result());
        for (int row = 0; row < size; row++) {
          for (int column = 0; column < size; column++) {
            if (game.isPlayable(row, column) && (row != size / 2 || column != size / 2)) {
              assertTrue(game.hasPeg(row, column));
            }
          }
        }
      }
    }
  }

  @Test
  void orthogonalMoveRemovesExactlyTheJumpedPeg() {
    SolitaireGame game = new SolitaireGame(new BoardShape.English(), 7);
    assertTrue(game.move(jump(1, 3, 3, 3)));
    assertFalse(game.hasPeg(1, 3));
    assertFalse(game.hasPeg(2, 3));
    assertTrue(game.hasPeg(3, 3));
    assertEquals(31, game.pegCount());
    assertEquals(1, game.moveCount());
    assertTrue(game.hasPeg(3, 2));
  }

  @Test
  void diagonalMoveIsAllowedByTheCourseRules() {
    SolitaireGame game = new SolitaireGame(new BoardShape.Diamond(), 9);
    assertTrue(game.move(jump(2, 2, 4, 4)));
    assertFalse(game.hasPeg(2, 2));
    assertFalse(game.hasPeg(3, 3));
    assertTrue(game.hasPeg(4, 4));
    assertEquals(39, game.pegCount());
  }

  @Test
  void invalidMovesDoNotChangeBoardOrMoveCount() {
    SolitaireGame game = new SolitaireGame(new BoardShape.English(), 7);
    List<Move> before = game.legalMoves();
    Move[] invalid = {jump(3, 3, 3, 5), jump(1, 3, 2, 3), jump(1, 3, 3, 2),
        jump(1, 3, 1, 5), jump(-1, 3, 1, 3), jump(3, 1, 3, 7), null};
    for (Move move : invalid) {
      assertFalse(game.move(move));
    }
    assertEquals(32, game.pegCount());
    assertEquals(0, game.moveCount());
    assertEquals(before, game.legalMoves());
    assertTrue(game.move(jump(1, 3, 3, 3)));
    assertFalse(game.move(jump(0, 3, 2, 3))); // Intermediate peg was removed.
  }

  @Test
  void completeGamesTerminateAndKeepValidCountsAcrossAllLayouts() {
    Random random = new Random(449);
    for (BoardShape shape : List.of(new BoardShape.English(), new BoardShape.Hexagon(),
        new BoardShape.Diamond())) {
      for (int size : new int[] {7, 9}) {
        for (int trial = 0; trial < 10; trial++) {
          SolitaireGame game = new SolitaireGame(shape, size);
          int initial = game.pegCount();
          while (!game.isOver()) {
            List<Move> moves = game.legalMoves();
            assertTrue(game.move(moves.get(random.nextInt(moves.size()))));
            assertEquals(initial - game.moveCount(), game.pegCount());
          }
          assertTrue(game.legalMoves().isEmpty());
          assertEquals(new PlayerRating().rate(game.pegCount()), game.result());
          assertFalse(game.move(jump(1, 3, 3, 3)));
          SolitaireGame fresh = new SolitaireGame(shape, size);
          assertEquals(initial, fresh.pegCount());
          assertEquals(0, fresh.moveCount());
        }
      }
    }
  }

  @Test
  void unsupportedSizesAreRejected() {
    for (int size : new int[] {-1, 0, 6, 8, 100}) {
      assertThrows(IllegalArgumentException.class,
          () -> new SolitaireGame(new BoardShape.English(), size));
    }
  }
}
