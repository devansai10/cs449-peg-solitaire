# Peg Solitaire BrainVita Sprint 3

Java 21, Swing, Maven, and JUnit 5 implementation of the CS 449 Sprint 3 game.

## Start

```sh
mvn clean test
mvn package
java -jar target/peg-solitaire-brainvita-1.0-SNAPSHOT.jar
```

On macOS, `Play.command` performs the package and launch commands.

## Play

1. Choose English, Hexagon, or Diamond and size 7 or 9.
2. Choose Manual or Automated mode.
3. Select `Record this game` before `New Game` when a saved replay is needed.
4. Click `New Game` to apply the settings.
5. In Manual mode, select a peg and then a legal empty destination. Use
   `Randomize` whenever the current manual board should be rearranged.
6. In Automated mode, click `Autoplay` and the computer will make legal moves
   until the game is over.
7. Click `Save Recording` to write the current recorded frames to a text file.
8. Click `Replay Recording` to select a saved text file and animate its frames.

Randomization preserves the playable board, number of pegs, and move count.
Recordings preserve board type, size, game mode, action labels, move counts, and
every displayed board state.

## Design

`SolitaireGame` owns the rules, board state, legal moves, randomization, and
snapshot validation. `GameSession` contains behavior common to all modes.
`ManualGameSession` accepts a player-selected move and permits randomization.
`AutomatedGameSession` selects a legal move using a supplied random generator.
`GameRecording` saves and validates the human-readable text format and restores
any recorded frame. `SolitaireApp` is only responsible for Swing interaction,
timers, file selection, and rendering.

## Tests

`Sprint3AcceptanceTest` covers manual play, randomization, automated completion,
recording/replay round trips, and invalid recording files. Earlier move, layout,
rating, and Sprint 2 acceptance tests remain in the suite.
