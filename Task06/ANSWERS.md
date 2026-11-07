# Task 6 - Design Challenge: Campus Event Registration Desktop Application (K5 - Evaluate, 13 marks)

**Student:** Syed Aakif Hussain S A | **Register No.:** 192525472

Source: `src/eventreg/{model,dao,service,ui,app}`, SQL script: `sql/schema.sql`.
Evidence: `output/RegistrationTests_output.txt` (19 PASS), `output/ScreenshotHarness_output.txt` + `output/01..07_*.png`, `output/registrations_export.csv`, `output/LoadBench_output.txt`.

> **Important honesty note.** The machine used to prepare this repository has no MySQL server and no desktop. `JdbcRegistrationDao` (MySQL, JDBC) is written and compiles, but it was **not run against a live MySQL here**.
> The demonstrations use `InMemoryRegistrationDao`, which implements the same `RegistrationDao` interface and the same rules (UNIQUE student+event, LIMIT/OFFSET paging), and the Swing screens were painted off-screen.
> Before submitting: run `sql/schema.sql` in MySQL, start the program with `-Ddb.url=...` (see README), repeat the normal / invalid / duplicate registrations and take real screenshots.

## Stage 1 - Interpret the problem (2 marks)

**Users:** (1) *committee member* - registers students through the form, views, filters and exports registrations; (2) *student* - is the person being registered (appears as data; the desktop application is operated by the committee).
**Screens:** Register screen; Registrations screen (table, filter, paging); dialogs: file chooser (export), About, error/confirmation messages.
**Data:** `events(event_id, name, event_date)`; `registrations(reg_id, student_id, student_name, email, event_id, registered_at)` with `UNIQUE(student_id, event_id)`.

**Requirements as testable statements**

| ID | Requirement | Test / evidence |
|---|---|---|
| R1 | A student ID that is not exactly 9 digits is rejected with "Student ID must be exactly 9 digits"; nothing is saved. | T5, T11; screenshot 05 |
| R2 | A name that is not 2-60 letters is rejected. | T6; screenshot 05 |
| R3 | An email without a valid `x@y.zz` form is rejected. | T7; screenshot 05 |
| R4 | An event must be selected, otherwise "Select an event". | T8; screenshot 05 |
| R5 | When several fields are wrong, all errors are shown together. | T9; screenshot 05 |
| R6 | The same student registering for the same event a second time is rejected with "already registered" and the row count does not change; the same student for a different event is accepted. | T2, T3, T4, T16; screenshot 06 |
| R7 | A valid registration is saved, a confirmation is shown, and the row appears in the table. | T1; screenshots 03, 04 |
| R8 | The table shows 10 rows per page, with Prev/Next and "Page x of y (n registrations)"; a page number beyond the end shows the last page. | T12, T13, T14; screenshot 04 |
| R9 | Choosing an event in the filter shows only that event's rows; "All events" shows all. | T15; screenshot 07 |
| R10 | Export writes a header and one CSV line per registration; commas are quoted and a leading `=` is neutralised. | T17, T18, T19; `registrations_export.csv` |
| R11 | While the database is being read or written the window keeps painting and the Register/Prev/Next controls are disabled until the work ends. | screenshot 02 ("Saving...") |
| R12 | A database failure shows a message and leaves no half-saved data. | DAO maps `SQLException` to a message; the insert is a single statement |
| R13 | Two clients registering the same student for the same event at the same moment produce exactly one row. | T16 (8 threads: 1 saved, 7 rejected); `UNIQUE` constraint in MySQL |

## Stage 2 - Screens and event flow (2 marks)

**Menu bar** (`JMenuBar`): **File** > Export CSV..., Exit; **View** > Register (Ctrl+R), Registrations (Ctrl+L); **Help** > About.

```
Register screen                                         Registrations screen
+----------------------------------------------+        +------------------------------------------------+
| File  View  Help                             |        | File  View  Help                               |
|----------------------------------------------|        |------------------------------------------------|
|  Register for an event                       |        | Event: [ All events v ]        Loading...      |
|  Student ID (9 digits) [_______________]     |        |+------+------------+-------------+-------+----+|
|  Full name             [_______________]     |        || ID   | Student ID | Name        | Email |Event|
|  Email                 [_______________]     |        |+------+------------+-------------+-------+----+|
|  Event                 [ v ]                 |        ||  ... 10 rows per page ...                    ||
|          [Register] [Clear]                  |        |+----------------------------------------------+|
|  (message: confirmation or errors)           |        |       [Prev]  Page 1 of 3 (25)  [Next]         |
|----------------------------------------------|        |------------------------------------------------|
| status line                                  |        | status line                                    |
+----------------------------------------------+        +------------------------------------------------+
```

**Flow between screens and dialogs**

```
 start --> (background: load events + first page) --> REGISTER screen
 REGISTER --[View > Registrations | Ctrl+L]--> LIST screen (reloads the current page)
 LIST     --[View > Register | Ctrl+R]------> REGISTER screen
 REGISTER --[Register button / Enter]--> validate -> duplicate? -> save --> message on the same screen
                                                                   '--> success: LIST data refreshed (last page)
 any      --[File > Export CSV...]--> file chooser --> background export --> status line "Exported n registrations to ..."
 any      --[Help > About]-----------> About dialog
 any      --[File > Exit | window close]--> exit
 LIST     --[Filter combo changes]--> page 1 of that event;  [Prev]/[Next] --> page - 1 / page + 1
```

## Stage 3 - Critical design decisions (2 marks)

| Decision | Where it happens | Reason |
|---|---|---|
| **Input validation** | `RegistrationService.register` (service layer). The form only collects text and shows errors. | One place, testable without a window, runs before any database call. All errors are collected and returned together. |
| **Duplicate checking** | Two layers: (1) `RegistrationService` calls `dao.exists(student, event)` for a friendly message; (2) the database `UNIQUE (student_id, event_id)` constraint is the final guard, and `JdbcRegistrationDao` converts `SQLIntegrityConstraintViolationException` into `DuplicateRegistrationException`. | Check-then-insert alone has a race (two clients both see "not found"). The constraint makes the rule true even then (test T16). |
| **Database access** | Only in `JdbcRegistrationDao` (interface `RegistrationDao`). All statements are `PreparedStatement`; connections are closed by try-with-resources. | No SQL in the UI, no SQL injection, easy to replace by the in-memory DAO for tests. |
| **Keeping long-running work off the event-dispatch thread** | Every database call (load events, load a page, register, export) runs in `SwingWorker.doInBackground()`. Swing components are touched only in `done()` (on the event thread). Controls are disabled while the work runs (`busy` flag) so a second click cannot start a second save. | The window keeps repainting ("Saving..." is visible); it never shows "Not responding". |
| **Large tables** | The table is loaded one page (10 rows) at a time with `LIMIT ? OFFSET ?` and a `COUNT(*)`. Export reads 500 rows at a time. | Memory and load time do not depend on the number of registrations (see the analysis below). |

## Stage 4 - Classes and handlers (4 marks)

**Layered class diagram**

```
 UI layer (Swing, package eventreg.ui)         Service layer (eventreg.service)        Data-access layer (eventreg.dao)
 +--------------------------------+            +-------------------------------+       +-----------------------------------+
 | MainFrame (JFrame + JMenuBar)  |            | RegistrationService           |       | <<interface>> RegistrationDao     |
 |   AppPanel (CardLayout)        |  calls     |  register(id,name,email,event)|calls  |  eventNames()  exists(id,event)   |
 |    +- RegisterForm  ---------->|----------->|  page(filter, page) -> Page   |------>|  insert(...)   page(f,limit,off)  |
 |    +- RegistrationsView ------>|            |  exportCsv(file, filter)      |       |  count(filter)                    |
 |  SwingWorker per DB action     |            |  ValidationException          |       +----------+------------------------+
 +--------------------------------+            +-------------------------------+                  |
        no SQL, no rules                       no Swing, no SQL                        +------------+--------------+
                                                                                       | JdbcRegistrationDao (MySQL)  |
 Model (eventreg.model): Registration, PageResult (records)                            | InMemoryRegistrationDao      |
 App (eventreg.app): Main (chooses the DAO), SampleData, tests, harness, benchmark     +------------------------------+
```

**Register action**

```
ALGORITHM RegisterAction
INPUT : form fields id, name, email, event
OUTPUT: message on the form; table refreshed when saved
BEGIN  (event-dispatch thread)
  1. IF busy THEN RETURN
  2. busy <- TRUE; DISABLE Register button; SHOW "Saving..."
  3. COPY the field values into local variables
  4. START background worker W:
  5.     (background thread)
  6.     errors <- EMPTY list
  7.     IF id is not exactly 9 digits THEN ADD "Student ID must be exactly 9 digits" to errors
  8.     IF name is not 2-60 letters THEN ADD error;  IF email is not valid THEN ADD error;  IF event is empty THEN ADD error
  9.     IF errors is not empty THEN RAISE ValidationException(errors)
 10.     IF dao.exists(id, event) THEN RAISE DuplicateRegistration
 11.     dao.insert(id, name, email, event)          // UNIQUE constraint may still raise DuplicateRegistration
 12. WHEN W has finished (event-dispatch thread):
 13.     IF no exception THEN SHOW confirmation; CLEAR text fields; reload table (last page)
 14.     ELSE IF ValidationException THEN SHOW every error
 15.     ELSE IF DuplicateRegistration THEN SHOW "already registered"
 16.     ELSE SHOW "Database problem ... Nothing was saved."
 17.     ENABLE Register button; busy <- FALSE
END
```

**Filter action**

```
ALGORITHM FilterAction
INPUT : selected item s of the filter combo, requested page p
OUTPUT: table showing one page of the selected event
BEGIN  (event-dispatch thread)
  1. IF filter combo is being rebuilt by the program THEN RETURN
  2. eventFilter <- NULL IF s = "All events" ELSE s
  3. DISABLE Prev, Next and the filter; SHOW "Loading..."
  4. START background worker W:
  5.     total <- dao.count(eventFilter)
  6.     pages <- MAX(1, CEILING(total / 10));  p <- CLAMP(p, 0, pages - 1)
  7.     rows  <- dao.page(eventFilter, limit = 10, offset = p * 10)     // SELECT ... WHERE (? IS NULL OR e.name = ?) ORDER BY reg_id LIMIT ? OFFSET ?
  8.     RETURN (rows, total, p, pages)
  9. WHEN W has finished (event-dispatch thread):
 10.     REMOVE all table rows; ADD the returned rows
 11.     SHOW "Page p+1 of pages (total registrations)"
 12.     ENABLE Prev IF p > 0; ENABLE Next IF p < pages - 1; ENABLE the filter; CLEAR "Loading..."
END
```

## Stage 5 - Implementation and demonstration (3 marks)

Swing components used: `JFrame`, `JPanel`, `JTextField`, `JComboBox`, `JTable` (`DefaultTableModel`), `JMenuBar`/`JMenu`/`JMenuItem`, `JButton`, `JLabel`, `JScrollPane`, `JFileChooser`, `JOptionPane`, `SwingWorker`; JDBC in `JdbcRegistrationDao`. SQL script: `sql/schema.sql`.

| Test case | Input | Result observed (`ScreenshotHarness_output.txt`) | Screenshot |
|---|---|---|---|
| Start-up | - | events and first page loaded in the background | `01_register_screen.png` |
| **Normal** | 192525100, Syed Aakif Hussain, aakif@example.edu, Hackathon | "Saving..." shown immediately; then "Registered 192525100 for Hackathon." | `02_normal_registering_in_progress.png`, `03_normal_registration_success.png` |
| Normal - table | View > Registrations | page 3 of 3, 6 rows; the new row (ID 26) is listed | `04_registrations_table_new_row_last_page.png` |
| **Invalid** | id `12ab`, name `A`, email `bad`, no event | four errors listed together; nothing saved | `05_invalid_input_errors.png` |
| **Duplicate** | same student and event again | "Not registered: Student 192525100 is already registered for Hackathon." | `06_duplicate_registration_rejected.png` |
| Filter | Dance Fest | 6 rows, only that event | `07_filter_by_event.png` |
| Export | File > Export | header + 26 rows written | `registrations_export.csv` |
| Logic tests | 19 cases incl. 8 simultaneous duplicates | 19 passed, 0 failed | `RegistrationTests_output.txt` |

The screenshots are the real Swing screens (menu bar + panel) painted off-screen by `ScreenshotHarness` while the real SwingWorkers ran.
For the MySQL demonstration: create the database with `sql/schema.sql`, run `java -Ddb.url=jdbc:mysql://localhost:3306/campus_events -Ddb.user=root -Ddb.password=... -cp "out:mysql-connector-j.jar" eventreg.app.Main`, and take screenshots of one normal, one invalid and one duplicate registration.

## Alternative Concept and Complexity Analysis

**Alternative:** load all registrations into the `JTable` at once (`SELECT * FROM registrations`, then `model.addRow` for every row), instead of one page at a time with `LIMIT/OFFSET` in a `SwingWorker`:

```java
List<Registration> all = dao.page(null, Integer.MAX_VALUE, 0);          // every row crosses the network
DefaultTableModel model = new DefaultTableModel(columns, 0);
for (Registration r : all) model.addRow(new Object[]{ ... });            // 100,000 rows in memory
table.setModel(model);
```

Measured with `LoadBench` for 100,000 registrations (in-process store, one run):

| Approach | Time | Extra heap |
|---|---|---|
| A. load all 100,000 rows into the table model | 80 ms | 9.3 MB |
| B. one page of 10 rows, first page (OFFSET 0) | 0.005 ms | about 0 |
| B. one page, middle (OFFSET 50,000) | 1.08 ms | about 0 |
| B. one page, last (OFFSET 99,990) | 1.79 ms | about 0 |

(The store here already holds the rows and shares their strings with the table model, so the 9.3 MB is only the model's own arrays and boxed IDs. With a real database the rows are new objects: expect several times more, roughly 20-30 MB for 100,000 rows, plus the transfer time.)

- **Loading time:** A is `O(N)` rows read, transferred and converted (N = 100,000); with MySQL over a network this is seconds. B is `O(offset + page size)` for the database work, and `O(10)` for the UI: it reads past `offset` rows (a deep page costs more, as the table shows), but never more than needed. A `COUNT(*)` for "Page x of y" is `O(N)` in the worst case but is a very fast index-only scan. If deep pages become slow, keyset paging (`WHERE reg_id > ? ORDER BY reg_id LIMIT 10`) makes every page `O(page size)`.
- **Memory:** A holds `O(N)` rows in the model (`JTable` draws only the visible rows, but the model keeps all of them); B holds `O(page size)` = 10 rows whatever N is.
- **Responsiveness:** A on the event thread freezes the window for the whole load (80 ms here with no network; seconds with MySQL). Even in a `SwingWorker`, A leaves the user with nothing to see until all rows arrive, and the table model update itself still happens on the event thread. B shows "Loading..." and a new page within milliseconds; the window never freezes because the work is in `doInBackground()` and Swing is touched only in `done()`.
- **Where A is acceptable:** a few hundred rows (a small event). For this application, which must keep working as registrations grow, B (paging with a `SwingWorker`) is the right design.
