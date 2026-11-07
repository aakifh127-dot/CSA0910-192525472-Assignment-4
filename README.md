# CSA09-192525472-Assignment4

**Event-Driven GUI Design (CO4): AWT, Swing, event delegation, layouts, menus and GUI-JDBC integration**

**Student:** Syed Aakif Hussain S A  |  **Register No.:** 192525472  |  Course: Programming in Java (CSA09)  |  SIMATS Engineering

Compiled and run with Java 21.0.12.1

GitHub repository link: ______________________________ (fill in before submitting)

## Folder layout

Each task has its own folder:

```
TaskNN/
  src/        Java source code
  input/      input files (empty for this assignment)
  output/     program output (.txt), console screenshots (.png) and images of the screens
  ANSWERS.md  the written answers for that task (diagnoses, tables, diagrams, pseudocode, comparisons)
  README.md   what the task contains and how to run it
  run.sh      compile and run commands
```

| Folder | Task | Marks | Main programs |
|---|---|---|---|
| [Task01](Task01/) | GUI Design Review | 6 | FeeService, FeeWindow, FeeServiceTest, DispatchBench |
| [Task02](Task02/) | From Requirement to Event-Driven Design | 8 | AppointmentDesk, AppointmentService, CatalogBench |
| [Task03](Task03/) | From Sketch to Layout | 9 | LibraryPanel, LibraryFrame, ResizeCheck, LayoutBench |
| [Task04](Task04/) | Event-Handling Forensics | 7 | DrawPadBuggy, DrawPad, DrawPadBuffered, PaintCheck |
| [Task05](Task05/) | Same Screen, Different Event-Handling Styles | 7 | ConverterA-D, ConverterA2-D2, Converter, ConverterShared |
| [Task06](Task06/) | Campus Event Registration (Swing + JDBC) | 13 | eventreg.* (UI / service / DAO), sql/schema.sql |

## Running

Requires JDK 17 or later (records are used).

```
cd Task04
sh run.sh
```

`run.sh` compiles and runs the tests, checks and benchmarks and writes `output/*_output.txt`. The windows themselves (`FeeWindow`, `AppointmentDesk`, `LibraryFrame`, `DrawPad`, `Converter`, `eventreg.app.Main`)
need a desktop; their start commands are in each task's README. Task 6 uses MySQL when `-Ddb.url` is given (run `Task06/sql/schema.sql` first), otherwise in-memory demo data.

## What you must still do yourself

1. Create the GitHub repository `CSA09-192525472-Assignment4`, share it with the faculty and **commit task by task** (the commit history is checked).
2. Run each window on your computer and add your own screenshots next to the generated images, each linked to the requirement it shows.
3. Run Task 6 against a real MySQL database and screenshot a normal, an invalid and a duplicate registration.
4. Check the pseudocode against the SIMATS Standard for Algorithmic Pseudocode and fill in the submission date and repository link.

## Suggested commit history

One commit per task, for example: `Task01: diagnose and repair fee window`, `Task02: appointment desk design and tests`, `Task03: library layout and resize evidence`,
`Task04: DrawPad forensics and repair`, `Task05: four listener styles and comparison`, `Task06: layered Swing-JDBC registration app`.
