
# Hero List and Console Menu Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Load heroes at startup, show them in a console menu, and save the list on exit.

**Architecture:** Keep `Hero` as the immutable model. A `HeroRepository` owns JSON loading/saving, using a bundled `heroes.json` template only when the writable `data/heroes.json` file is missing. A testable `ConsoleMenu` handles display and input; `Main` wires the repository and menu together.

**Tech Stack:** Java 25, Maven, Gson 2.11.0, JUnit Jupiter 5.12.2.

**Spec:** Chat-approved design on 2026-11-09: `Hero` gains `name`; load a starter roster from a resource, keep mutable runtime data in `data/heroes.json`, show-all and exit menu, save on exit. Empty initial roster is the default until starter heroes are provided.

## Global Constraints

- The model fields are `String name`, `int strength`, `String rarity`, `String heroClass`, `String element`, and `boolean healer`.
- The writable roster is `data/heroes.json`; do not write into the packaged classpath resources.
- When the writable roster does not exist, initialize it from `src/main/resources/heroes.json`.
- Menu choices are `1` (show all heroes) and `0` (exit and save).
- JSON is UTF-8; use Gson 2.11.0 and `TypeToken<List<Hero>>` for generic list deserialization.
- Do not add hero creation/editing menu actions in this change.

## Review Focus

- Missing runtime file: copy the bundled starter list and load it.
- Empty roster: show a clear empty-list message without crashing.
- Invalid or malformed JSON: report a useful error; do not silently replace user data.
- Unknown menu input: explain the accepted choices and continue the menu.
- Save failure on exit: report failure rather than claiming the roster was saved.

---

### Task 1: Model and test setup

**Files:**
- Modify: `pom.xml`
- Modify: `src/main/java/org/example/Hero.java`
- Modify: `src/test/java/org/example/HeroTest.java`

**Interfaces:**
- Produces: `Hero(String name, int strength, String rarity, String heroClass, String element, boolean healer)`.

- [ ] **Step 1: Add JUnit test for the name field**

Update the hero test to construct a hero with all six values and assert each record accessor, including `name()`.

- [ ] **Step 2: Run the test and verify it fails because `name()` is missing**

Run: `mvn test`
Expected: test compilation fails on the missing name constructor argument/accessor.

- [ ] **Step 3: Add JUnit Jupiter 5.12.2 and Surefire configuration; add `String name` to the record**

- [ ] **Step 4: Run `mvn test`**

Expected: the model test passes.

### Task 2: JSON roster repository

**Files:**
- Modify: `pom.xml`
- Create: `src/main/resources/heroes.json`
- Create: `src/main/java/org/example/HeroRepository.java`
- Create: `src/test/java/org/example/HeroRepositoryTest.java`

**Interfaces:**
- Consumes: the six-field `Hero` record from Task 1.
- Produces: `HeroRepository(Path dataFile)`, `List<Hero> load() throws IOException`, and `void save(List<Hero> heroes) throws IOException`.

- [ ] **Step 1: Add repository tests**

Test that a missing runtime file is initialized from the resource template, an existing roster is loaded, saving then loading preserves all Hero fields, malformed JSON is reported, and an unwritable destination produces an `IOException`.

- [ ] **Step 2: Run repository tests and verify they fail because the repository is missing**

Run: `mvn test`
Expected: compilation fails because `HeroRepository` does not exist.

- [ ] **Step 3: Add Gson 2.11.0 and implement repository load/save**

Use `TypeToken<List<Hero>>` to preserve the list element type. Copy the resource template only when the runtime file does not exist; never overwrite an existing file during load.

- [ ] **Step 4: Run `mvn test`**

Expected: all repository and model tests pass.

### Task 3: Console menu and application startup

**Files:**
- Modify: `src/main/java/org/example/Main.java`
- Create: `src/main/java/org/example/ConsoleMenu.java`
- Create: `src/test/java/org/example/ConsoleMenuTest.java`

**Interfaces:**
- Consumes: `HeroRepository.load()` and `HeroRepository.save(List<Hero>)` from Task 2.
- Produces: `ConsoleMenu(List<Hero> heroes, HeroRepository repository, Scanner input, PrintStream output)` and `void run()`.

- [ ] **Step 1: Add menu tests for displaying all heroes, empty roster, invalid input, and exit-triggered save**

Use injected input/output and a temporary data path; assert the visible output and that exiting persists the in-memory roster.

- [ ] **Step 2: Run menu tests and verify they fail because `ConsoleMenu` is missing**

Run: `mvn test`
Expected: compilation fails because `ConsoleMenu` does not exist.

- [ ] **Step 3: Implement `ConsoleMenu.run()` and wire `Main`**

Display `1 — Показать всех героев` and `0 — Выход`; print each hero's name, strength, rarity, class, element, and healer flag. Invalid input prints guidance and repeats. On `0`, save once and terminate. `Main` loads via `HeroRepository` before entering the menu.

- [ ] **Step 4: Run the full test suite and launch the app with scripted input**

Run: `mvn test`; then run the app with input `1\n0\n` and verify the roster displays and the writable JSON file exists.
Expected: all tests pass; runtime list is saved on exit.