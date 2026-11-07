# Task01 - GUI Design Review (diagnose and repair a poor event-handling design)

**Student:** Syed Aakif Hussain S A  |  **Register No.:** 192525472

## Contents

| Folder | What it holds |
|---|---|
| `src/` | Java source code |
| `input/` | No input file needed for this task |
| `output/` | Console output (`*_output.txt`), console screenshots (`*_console.png`) and, where the task has a screen, images of it |

## Source files

- `src/DispatchBench.java`
- `src/FeeException.java`
- `src/FeeRepository.java`
- `src/FeeService.java`
- `src/FeeServiceTest.java`
- `src/FeeWindow.java`
- `src/InMemoryFeeRepository.java`
- `src/JdbcFeeRepository.java`
- `src/Receipt.java`

## Written answers

- `ANSWERS.md` - diagnosis, repaired design, design defence, alternative analysis

## Compile and run

```
sh run.sh
```

or manually from inside this folder (see `run.sh` for the exact commands). Compiled and run with Java 21.0.12.1.
`FeeWindow` needs a desktop: `java -cp out FeeWindow`.

## Output

- `output/DispatchBench_console.png`
- `output/DispatchBench_output.txt`
- `output/FeeServiceTest_console.png`
- `output/FeeServiceTest_output.txt`

The `*_console.png` files are console-style images rendered from the real program output. Images of Swing screens were painted off-screen from the real components because the machine used had no desktop.
For submission, replace them with (or add) your own screenshots of the running windows, taken on your computer.
