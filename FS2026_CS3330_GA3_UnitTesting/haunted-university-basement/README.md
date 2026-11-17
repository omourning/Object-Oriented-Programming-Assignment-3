# Haunted University Basement

A deterministic Java 17 Maven backend project designed for students to write JUnit tests.

## Goal
Recover the **Lost Gradebook** and defeat the **Final Exam Phantom**.

## Design notes
- No console-driven gameplay loop is required for grading.
- The primary testing surface is `GameEngine`.
- Combat, traps, loot, and unlocking are deterministic.
- Students should write JUnit tests for movement, combat, inventory, traps, room unlocking, and quest progression.

## Suggested student tasks
- Write unit tests for `DamageCalculator`, `TrapResolver`, and `QuestTracker`
- Write integration tests against `GameEngine`
- Test happy paths and edge cases
- Aim for high line and branch coverage

## Main API
- `move(Direction direction)`
- `pickUpItem(String itemName)`
- `equipItem(String itemName)`
- `useItem(String itemName)`
- `unlockRoom(Direction direction)`
- `attack(String monsterName)`

## Win condition
The game is won only when both are true:
1. The player has obtained the `Lost Gradebook`
2. The `Final Exam Phantom` has been defeated

## Running Test
1. How to run the tests: right-click src/test/java and choose Run As -> JUnit Test.
2. How to run coverage: Coverage As -> JUnit Test.
3. How to run Pitest: Run As -> Maven -> Goals -> type "clean test-compile org.pitest:pitest-maven:mutationCoverage" -> Apply -> Run
4. Where the reports are (reports/coverage, reports/pitest).
