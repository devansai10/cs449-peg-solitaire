# Sprint 2 - AI-generated tests and review record

The professor requests evidence of the prompt and ChatGPT answer for at least two generated unit tests. This document records the exact prompts and the final generated methods. Matching transcript images are included in `evidence/`.

## Prompt 1

Act as a Java 21/JUnit 5 testing assistant. My Peg Solitaire BrainVita program uses `new SolitaireGame(BoardShape shape, int size)` and supports English, Hexagon and Diamond boards of sizes 7 and 9. All playable holes start with pegs except the center. A new instance represents the new board requested by the Swing New Game button. Write one JUnit 5 test verifying a move modifies the previous board, then selecting Diamond size 9 and starting a new game creates 40 pegs, an empty center, zero moves and no old result. Verify the previous instance is not changed. Use existing public methods only.

**Initial response review:** ChatGPT used API names that are not in this project: `BoardShape.ENGLISH`,
`BoardShape.DIAMOND`, `getPegCount()`, `getMoveCount()`, `getResult()`, and
`move(int, int, int, int)`.

## Prompt 1 correction

The test does not match my actual public API. Board shapes are created with
`new BoardShape.English()` and `new BoardShape.Diamond()`. The methods are
`pegCount()`, `moveCount()`, `result()`, `hasPeg(row, column)`, and
`move(new SolitaireGame.Move(new SolitaireGame.Position(fromRow, fromColumn),
new SolitaireGame.Position(toRow, toColumn)))`. Please rewrite the test using
only these exact public methods. The valid English size 7 move is from `(1,3)`
to `(3,3)`.

**Corrected generated method used:**
`Sprint2AcceptanceTest.restartingWithSelectedBoardResetsMovesAndCenter()`.

## Prompt 2

Act as a Java 21/JUnit 5 testing assistant. For `SolitaireGame`, positions are `(row,column)` and `move(new Move(from,to))` must reject an illegal move without changing ANY playable board cell or counters. Write one JUnit 5 unit test that snapshots all cells of English size 7, attempts an illegal jump from `(1,3)` to `(1,5)`, verifies the entire board and counts are unchanged, and then verifies a legal recovery move `(1,3)` to `(3,3)` succeeds. Use JUnit 5 assertions and the public API only.

**Generated method:** `Sprint2AcceptanceTest.invalidJumpLeavesEveryCellUnchangedAndAllowsRecovery()`.

## How the outputs were checked

- Compared API calls with the existing `SolitaireGame` and `BoardShape` public methods.
- Confirmed expected default and Diamond size-9 peg counts against the Sprint 1 acceptance criteria and source shape formulas.
- Compiled and executed the complete suite with Maven and JUnit 5.

**Correction review:** The first response required an API correction, described above. The
second response used the correct public API. After reviewing the coordinates, peg counts,
and assertions, the complete suite passed with Maven on October 27, 2026: 16 tests,
0 failures, 0 errors, and 0 skipped.

### Evidence files

- `evidence/AI_Test_1_Prompt_and_Response.png`
- `evidence/AI_Test_2_Prompt_and_Response.png`
- `evidence/Sprint2_Unit_Test_Results.png`
- `evidence/AI_Test_1_Prompt_Actual.png`
- `evidence/AI_Test_1_Response_Actual.png`
- `evidence/AI_Test_1_Correction_Actual.png`
- `evidence/AI_Test_2_Prompt_Actual.png`
- `evidence/AI_Test_2_Response_Actual.png`
- `evidence/Sprint2_Unit_Test_Results_Actual.png`
- `evidence/Sprint2_Game_Actual.png`
