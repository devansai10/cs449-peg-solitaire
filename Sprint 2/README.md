# CS 449 Sprint 2 - Peg Solitaire BrainVita

This folder contains the Sprint 2 Java 21, Swing, Maven, and JUnit 5 submission. It continues the Sprint 0 and Sprint 1 Peg Solitaire BrainVita project.

## Run

```sh
cd game
mvn clean test
mvn package
java -jar target/peg-solitaire-brainvita-1.0-SNAPSHOT.jar
```

The initial board is English size 7. Select English/Hexagon/Diamond and board size 7 or 9 and press New Game. Select a peg and then an empty target hole two cells away; a peg must occupy the intervening hole. Horizontal, vertical and diagonal jumps are allowed. New Game resets moves and results. Game over appears when one peg remains or there are no legal moves. The existing ratings are Outstanding/Very Good/Good/Average.

## Files

- `game/src/main/java/edu/cs449/solitaire/`: `BoardShape`, `PlayerRating`, `SolitaireGame`, `SolitaireApp` (Swing UI).
- `game/src/test/java/edu/cs449/solitaire/`: Sprint 1 tests plus four Sprint 2 acceptance tests. Two are explicitly AI-assisted.
- `Sai_Devan_Sprint2.docx`: completed report with implementation and test mappings.
- `evidence/`: Sprint 2 Maven, GUI, and AI-assisted test evidence.
- `AI_TEST_PROMPTS.md`: prompts, generated-test descriptions, and review notes.

## Verification

`mvn clean test` completed successfully on October 27, 2026. All 16 JUnit tests passed with no failures, errors, or skipped tests. The public GitHub URL was also checked with a read-only Git request.

## Still required before Canvas submission

1. Record and upload a narrated demonstration of no more than three minutes, then replace the video placeholder in the report.
2. Verify the video and GitHub links from a signed-out browser before uploading the DOCX to Canvas.

Autoplay, randomization and record/replay are not required until later sprints according to the Homework Overview.
