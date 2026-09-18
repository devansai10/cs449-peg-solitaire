# CS 449 Peg Solitaire BrainVita - Sprint 0

Repository: https://github.com/devansai10/cs449-peg-solitaire

Java 21, Swing, JUnit Jupiter 5.10.3, and Maven.

## Run

Install JDK 21 and Maven. Open this folder in Visual Studio Code; the Extension Pack for Java is useful but not needed for the commands below.

```sh
mvn test
java -cp target/classes edu.cs449.solitaire.SolitaireSetupDemo
```

The first test run downloads Maven dependencies. Swing requires a graphical desktop.

## Contents

- `src/main/java/edu/cs449/solitaire/PlayerRating.java`: rating logic.
- `src/test/java/edu/cs449/solitaire/PlayerRatingTest.java`: six JUnit tests.
- `src/main/java/edu/cs449/solitaire/SolitaireSetupDemo.java`: GUI demonstration.
- `report/CS449_Sprint0_Report.docx`: editable report with complete source listings.
- `evidence/unit-tests.txt`: Maven execution output with local personal paths redacted.
- `evidence/unit-tests-vscode.png`: direct screenshot of six passing JUnit tests executed in Visual Studio Code.
- `evidence/unit-tests-screenshot.png`: copy of the devansai console screenshot.
- `evidence/gui-screenshot.png`: screenshot of the running Swing window.

## Before submitting

1. Review and understand the source. Follow your course's rules on assistance and attribution.
2. Read the Google Java Style Guide and the JUnit tutorial linked in the report; the report does not claim that you have personally completed that study.
3. Run the GUI. Select Diamond, turn Record game off, and click Preview settings. Expected text: `Selected: Diamond | Recording: off`. Select English, turn recording on, and preview again. Verify only one radio button can be selected.
4. Give your instructor access to this private GitHub repository before submitting its link.

## Scope

This is a Sprint 0 settings demo, not the completed game. Board names and the recording preference are illustrative controls. Manual play, initialization, autoplay, randomization, and record/replay belong to later sprints. PlayerRating rejects zero and negative counts as an explicit design decision.

## Learning notes

- `@Test` marks a JUnit test method.
- `assertEquals` checks the returned rating; `assertThrows` checks invalid input handling.
- `ButtonGroup` makes radio buttons mutually exclusive.
- `JCheckBox` represents an independent on/off choice.
- `JSeparator` draws the required lines.
- The button's action listener reads the settings and updates a label.
- `SwingUtilities.invokeLater` creates the interface on Swing's event dispatch thread.
- The rating class has no dependency on Swing and can be tested independently.

## References

- https://google.github.io/styleguide/javaguide.html
- https://docs.junit.org/5.10.3/user-guide/index.html
- https://docs.oracle.com/javase/tutorial/uiswing/components/button.html
- https://docs.oracle.com/javase/tutorial/uiswing/components/separator.html
