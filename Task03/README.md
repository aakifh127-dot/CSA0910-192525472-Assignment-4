# Task03 - From Sketch to Layout (campus library search screen)

**Student:** Syed Aakif Hussain S A  |  **Register No.:** 192525472

## Contents

| Folder | What it holds |
|---|---|
| `src/` | Java source code |
| `input/` | No input file needed for this task |
| `output/` | Console output (`*_output.txt`), console screenshots (`*_console.png`) and, where the task has a screen, images of it |

## Source files

- `src/LayoutBench.java`
- `src/LibraryFrame.java`
- `src/LibraryPanel.java`
- `src/LibraryPanelAbsolute.java`
- `src/LibraryPanelGridBag.java`
- `src/ResizeCheck.java`

## Written answers

- `ANSWERS.md` - layout choices, nested diagram, resizing table, alternative analysis

## Compile and run

```
sh run.sh
```

or manually from inside this folder (see `run.sh` for the exact commands). Compiled and run with Java 21.0.12.1.
`LibraryFrame` needs a desktop: `java -cp out LibraryFrame`.

## Output

- `output/LayoutBench_console.png`
- `output/LayoutBench_output.txt`
- `output/Library_1100x750.png`
- `output/Library_480x360.png`
- `output/Library_800x600.png`
- `output/Library_absolute_1100x750.png`
- `output/Library_gridbag_1100x750.png`
- `output/ResizeCheck_console.png`
- `output/ResizeCheck_output.txt`

The `*_console.png` files are console-style images rendered from the real program output. Images of Swing screens were painted off-screen from the real components because the machine used had no desktop.
For submission, replace them with (or add) your own screenshots of the running windows, taken on your computer.
