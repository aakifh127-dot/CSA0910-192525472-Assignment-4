# Task02 - From Requirement to Event-Driven Design (hospital outpatient desk)

**Student:** Syed Aakif Hussain S A  |  **Register No.:** 192525472

## Contents

| Folder | What it holds |
|---|---|
| `src/` | Java source code |
| `input/` | No input file needed for this task |
| `output/` | Console output (`*_output.txt`), console screenshots (`*_console.png`) and, where the task has a screen, images of it |

## Source files

- `src/AppointmentDesk.java`
- `src/AppointmentService.java`
- `src/AppointmentTests.java`
- `src/BookingException.java`
- `src/CachedDoctorCatalog.java`
- `src/CatalogBench.java`
- `src/DbDoctorCatalog.java`
- `src/Doctor.java`
- `src/DoctorCatalog.java`
- `src/DoctorSource.java`

## Written answers

- `ANSWERS.md` - event table, component tree, pseudocode, event trace, alternative analysis

## Compile and run

```
sh run.sh
```

or manually from inside this folder (see `run.sh` for the exact commands). Compiled and run with Java 21.0.12.1.
`AppointmentDesk` needs a desktop: `java -cp out AppointmentDesk` (prints the event trace).

## Output

- `output/AppointmentTests_console.png`
- `output/AppointmentTests_output.txt`
- `output/CatalogBench_console.png`
- `output/CatalogBench_output.txt`

The `*_console.png` files are console-style images rendered from the real program output. Images of Swing screens were painted off-screen from the real components because the machine used had no desktop.
For submission, replace them with (or add) your own screenshots of the running windows, taken on your computer.
