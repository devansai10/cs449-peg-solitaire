# CS 449 Peg Solitaire BrainVita

Student: Devansai

## Sprint 3

The Sprint 3 implementation is in `Sprint 3/`. It adds manual and automated
game-mode classes, manual-board randomization, text recording, replay, and five
new JUnit acceptance tests. The complete suite contains 21 passing tests.

```sh
cd "Sprint 3/game"
mvn clean test
mvn package
java -jar target/peg-solitaire-brainvita-1.0-SNAPSHOT.jar
```

See `Sprint 3/README.md` for the feature checklist, completed report, LLM
evidence, and the remaining video step.

## Sprint 2

The Sprint 2 submission is in `Sprint 2/`. It contains the Java 21 Swing game, four acceptance tests added for Sprint 2, the report, and supporting evidence. The complete Maven suite contains 16 passing JUnit 5 tests.

```sh
cd "Sprint 2/game"
mvn clean test
mvn package
java -jar target/peg-solitaire-brainvita-1.0-SNAPSHOT.jar
```

See `Sprint 2/README.md` for the feature summary and recording checklist.

## Sprint 1

The manual game and its 12 unit tests are in `game/`. Java 21 and Maven are required.

```sh
cd game
mvn test
mvn package
java -jar target/peg-solitaire-brainvita-1.0-SNAPSHOT.jar
```

See `game/README.md` for game rules and design details. The final report and screenshots are in `sprint1/`. Earlier Sprint 0 work is preserved in `Sprint 0/`.
