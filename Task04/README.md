# Task04 - Event-Handling Forensics (DrawPad)

**Student:** Syed Aakif Hussain S A  |  **Register No.:** 192525472

## Contents

| Folder | What it holds |
|---|---|
| `src/` | Java source code |
| `input/` | No input file needed for this task |
| `output/` | Console output (`*_output.txt`), console screenshots (`*_console.png`) and, where the task has a screen, images of it |

## Source files

- `src/DrawPad.java`
- `src/DrawPadBuffered.java`
- `src/DrawPadBuggy.java`
- `src/PaintBench.java`
- `src/PaintCheck.java`
- `extras/DrawPadOverrideError.java`

## Written answers

- `ANSWERS.md` - defects, failing interaction, repair, @Override, alternative analysis

## Compile and run

```
sh run.sh
```

or manually from inside this folder (see `run.sh` for the exact commands). Compiled and run with Java 21.0.12.1.
`DrawPad` needs a desktop: `java -cp out DrawPad`. The harness uses `sun.misc.Unsafe` to test the listener and `paint()` without a window.

## Output

- `output/DrawPadBuggy_after_restore_model.png`
- `output/DrawPad_after_restore.png`
- `output/DrawPad_before_minimise.png`
- `output/OverrideError_console.png`
- `output/OverrideError_output.txt`
- `output/PaintBench_console.png`
- `output/PaintBench_output.txt`
- `output/PaintCheck_console.png`
- `output/PaintCheck_output.txt`

The `*_console.png` files are console-style images rendered from the real program output. Images of Swing screens were painted off-screen from the real components because the machine used had no desktop.
For submission, replace them with (or add) your own screenshots of the running windows, taken on your computer.
