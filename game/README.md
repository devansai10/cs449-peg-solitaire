# Peg Solitaire BrainVita

Playable manual game using Java 21 and Swing. Repository:
https://github.com/devansai10/cs449-peg-solitaire

## Start

From this folder:

```
mvn clean package
java -jar target/peg-solitaire-brainvita-1.0-SNAPSHOT.jar
```

Or double-click `Play.command` on macOS (requires Java 21 and Maven).

## Play

Choose English, Hexagon, or Diamond and size 7 or 9, then press New Game.
Click a peg, then an empty hole two positions away in a straight horizontal,
vertical, or diagonal direction. There must be a peg between the two holes;
that peg is removed. Clicking a different peg changes selection; clicking the
selected peg clears it. Legal destinations are highlighted when hints are on.
Settings take effect only when New Game is pressed; this resets the current game.
The game automatically ends when one peg remains or no legal jump remains.
Ratings: 1 Outstanding, 2 Very Good, 3 Good, 4+ Average.

## Board design

Sizes 7 and 9 are project choices, not an instructor-specified range. Size is
the number of indexed rows/columns. English uses three-hole-wide crossing arms;
Diamond uses Manhattan distance from the center; Hexagon uses an axial hexagonal
mask with visually offset rows. For all types, jumps use the same eight straight
row/column directions, including diagonals, as allowed by the project overview.
All playable positions initially contain pegs except the central hole.

## Structure and checks

`BoardShape` and its subclasses define layouts. `SolitaireGame` owns state,
validation, legal-move enumeration and game-over detection. `PlayerRating`
calculates ratings. `SolitaireApp` displays the game and sends clicks to the model.
Run `mvn test` for initialization, moves, invalid inputs, complete-game simulations
and rating tests. No GUI library is needed by the rules classes.

This version implements manual play. Autoplay, randomization, and saved
record/replay are future sprint features and are not included yet.
