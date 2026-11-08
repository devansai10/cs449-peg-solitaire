# CS 449 Sprint 3 Peg Solitaire BrainVita

Sprint 3 expands the Sprint 2 game with manual and automated game modes,
randomization, text-file recording, and replay. The Sprint 2 folder remains
unchanged.

## Run and test

```sh
cd "Sprint 3/game"
mvn clean test
mvn package
java -jar target/peg-solitaire-brainvita-1.0-SNAPSHOT.jar
```

The current suite contains 21 passing JUnit 5 tests: the 16 earlier tests and
5 Sprint 3 acceptance tests.

## Sprint 3 requirement status

- Board type and size selection: complete.
- Manual and automated mode selection: complete.
- New game with selected settings: complete.
- Manual moves and manual game-over handling: complete.
- Automated legal moves and automated game-over handling: complete.
- Manual-game randomization: complete; peg count and move count are preserved.
- Record a game to a readable text file: complete.
- Replay a saved text recording: complete.
- Automated tests for both game modes, randomization and replay: complete.

## Submission status

- Sprint 3 report tables and source-code mappings: complete.
- Manual/automated class-hierarchy discussion: complete.
- LLM design-principle prompts, responses, screenshots, and interpretation:
  complete in `evidence/` and the report.
- Manual functional verification: complete.
- Narrated demonstration of no more than five minutes: still to be recorded.
- Add the final video link to the report before uploading the DOCX to Canvas.
