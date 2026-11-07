# Task 2 - From Requirement to Event-Driven Design (K4 - Analyse, 8 marks)

**Student:** Syed Aakif Hussain S A | **Register No.:** 192525472

Files: `src/AppointmentDesk.java` (Swing window, prints a trace line from every handler), `src/AppointmentService.java` (booking rules),
`src/CachedDoctorCatalog.java` / `src/DbDoctorCatalog.java` (the two ways of getting doctors), `src/DoctorSource.java` (simulated doctors table),
`src/AppointmentTests.java`, `src/CatalogBench.java`.
Evidence: `output/AppointmentTests_output.txt` (13 PASS), `output/CatalogBench_output.txt`.

## Task 2.1 - Model the events (2 marks)

| Event source | Event object | Listener interface | Action taken |
|---|---|---|---|
| Window being shown (first time) | `WindowEvent` (`windowOpened`) | `WindowListener` via `WindowAdapter` | Traced only in the program; a natural place to give focus to the Patient ID field. |
| Patient ID `JTextField`, a key typed | `KeyEvent` (`keyTyped`) | `KeyListener` via `KeyAdapter` | Set `bookingInProgress = true` (a booking has been started). |
| Patient ID `JTextField`, Enter pressed | `ActionEvent` | `ActionListener` | Move on to the next field (the program only traces it). |
| Department `JComboBox`, selection changed | `ActionEvent` | `ActionListener` | `refreshDoctors()`: clear the doctor list and refill it with the doctors of that department only. |
| Book `JButton`, clicked | `ActionEvent` | `ActionListener` | `bookAppointment()`: validate everything, book, show confirmation or error in the message label. |
| Window close button | `WindowEvent` (`windowClosing`) | `WindowListener` via `WindowAdapter` | If a booking is in progress ask "Close anyway?" with `JOptionPane.showConfirmDialog`; `dispose()` only on Yes. |
| Window after `dispose()` | `WindowEvent` (`windowClosed`) | `WindowListener` | Release resources (nothing else to do here). |
| Confirm dialog buttons | handled inside `JOptionPane` | - | Returns `YES_OPTION` / `NO_OPTION` to `confirmAndClose()`. |

Design decision: the **doctor** and **time-slot** combo boxes need no listener. Their value is only needed when Book is pressed,
so it is read at that moment. This keeps the number of handlers small and avoids half-updated state.
(AWT equivalent: `Choice` fires `ItemEvent` with `ItemListener`; Swing `JComboBox` also supports `ItemListener`.)

## Task 2.2 - Component hierarchy (2 marks)

```
JFrame  "Outpatient Appointment Desk"                       layout: BorderLayout
|
+-- CENTER: JPanel  form                                    layout: GridLayout(5 rows, 2 columns)
|     +-- JLabel "Patient ID"        JTextField patientId
|     +-- JLabel "Department"        JComboBox<String> department
|     +-- JLabel "Doctor"            JComboBox<String> doctor         <- contents depend on department
|     +-- JLabel "Time slot"         JComboBox<String> slot
|     +-- JLabel (empty)             JButton book
|
+-- SOUTH: JLabel message                                    green for confirmation, red for error

(not in the tree) JOptionPane confirm dialog, created on demand when the window is closed during a booking
```

AWT equivalents if AWT is required: `Frame`, `Panel`, `Label`, `TextField`, `Choice`, `Button`, and `Dialog` for the confirmation.

## Task 2.3 - Handler logic (3 marks)

The pseudocode uses the numbered `ALGORITHM / INPUT / OUTPUT / BEGIN / END` form. *(Re-format it to the exact SIMATS Standard for Algorithmic Pseudocode before submitting if your copy of the standard differs.)*

**(a) Refresh the doctor list when the department changes**

```
ALGORITHM RefreshDoctors
INPUT : selected department d (may be empty), doctor catalogue C
OUTPUT: doctor list containing only the doctors of d
BEGIN
  1. REMOVE all items from the doctor list
  2. IF d is empty THEN RETURN
  3. names <- C.doctorsFor(d)                 // one Map lookup, O(1)
  4. FOR EACH name IN names DO
  5.     ADD name to the doctor list
  6. END FOR
  7. SET doctor selection to "nothing selected"
  8. SET bookingInProgress <- TRUE
END
```

**(b) Validate and book the appointment**

```
ALGORITHM BookAppointment
INPUT : patientId, department, doctor, slot, set Booked of "doctor|slot" keys
OUTPUT: confirmation message or error message
BEGIN
  1. id <- TRIM(patientId)
  2. IF id is empty THEN SHOW error "Patient ID is required"; RETURN
  3. IF id is not 4-12 letters/digits THEN SHOW error "Patient ID must be 4-12 letters or digits"; RETURN
  4. IF department is not selected THEN SHOW error "Select a department"; RETURN
  5. IF doctor is not selected THEN SHOW error "Select a doctor"; RETURN
  6. IF doctor NOT IN C.doctorsFor(department) THEN SHOW error "doctor does not work in department"; RETURN
  7. IF slot is not selected THEN SHOW error "Select a time slot"; RETURN
  8. key <- doctor + "|" + slot
  9. IF key IN Booked THEN SHOW error "doctor is already booked at slot"; RETURN
 10. ADD key to Booked
 11. SHOW confirmation "Booked: appointment A<n> for id with doctor at slot"
 12. SET bookingInProgress <- FALSE; CLEAR the Patient ID field
END
```
Implemented in `AppointmentDesk.refreshDoctors()` / `bookAppointment()` and `AppointmentService.book()`; 13 tests cover each branch (`AppointmentTests`).

## Task 2.4 - Trace the event sequence (1 mark)

Order of calls from opening to closing for a session in which the receptionist types an ID, picks a department, books, then closes the window
(this is the order produced by the `trace(...)` lines in `AppointmentDesk`; the exact position of the activate/deactivate lines can differ slightly between operating systems):

| Step | Thread | Call |
|---|---|---|
| 1 | main | `main` -> `SwingUtilities.invokeLater(...)` (the window is built on the event-dispatch thread) |
| 2 | EDT | `AppointmentDesk` constructor: components created, listeners registered, `pack()` |
| 3 | EDT | `setVisible(true)` -> native window created |
| 4 | EDT | `windowOpened()` (only the first time the window is shown) |
| 5 | EDT | `windowActivated()`; first `paint` of all components |
| 6 | EDT | user types in Patient ID: `keyTyped()` for each character |
| 7 | EDT | user chooses a department: `department` `actionPerformed()` -> `refreshDoctors()` |
| 8 | EDT | user clicks Book: `book` `actionPerformed()` -> `bookAppointment()` -> message label updated -> repaint |
| 9 | EDT | user clicks the close button: `windowClosing()` -> `confirmAndClose()` (dialog if a booking is in progress) |
| 10 | EDT | answer Yes: `dispose()` -> `windowDeactivated()` (may arrive here) -> `windowClosed()` |

With `DO_NOTHING_ON_CLOSE` the close button only produces `windowClosing`; the program decides whether to call `dispose()`.
To confirm on your machine: `java -cp out AppointmentDesk` prints these lines in the console (add a screenshot of the console for the submission).

**Comparison with the applet life cycle**

| Applet method | Called by | Nearest window event / method here |
|---|---|---|
| `init()` | browser, once, when the applet is loaded | the constructor (build components, register listeners) and `windowOpened` |
| `start()` | browser, each time the page becomes visible | `windowActivated` / `windowDeiconified` (window is usable again) |
| `paint(Graphics)` | AWT when the display must be redrawn | the same: `paint()` / `repaint()` on the event thread |
| `stop()` | browser, when the page is left | `windowDeactivated` / `windowIconified` |
| `destroy()` | browser, once, when unloaded | `windowClosing` -> `dispose()` -> `windowClosed` |

An applet's methods are called by its container in a fixed order. A window application is driven by the user: the same
`WindowEvent` methods arrive only when something happens, and the programmer decides what to do (for instance, refuse to close).
Applets are deprecated (for removal since Java 17), so new code uses frames and the event delegation model directly.

## Alternative Concept and Complexity Analysis

**Alternative:** fetch the doctors from the database every time the department changes (instead of loading them once into a
`Map<String, List<String>>`). `DbDoctorCatalog` is the same interface with a different body:

```java
// chosen: Map loaded once                         // alternative: query on every change
public List<String> doctorsFor(String dept) {      public List<String> doctorsFor(String dept) {
    return byDepartment.getOrDefault(dept,              return source.query(dept);
                                     List.of());          // SELECT name FROM doctors WHERE department = ?
}                                                   }
```

`DoctorSource` is a *simulated* doctors table (an unindexed scan of all m rows plus a 200 microsecond round trip); a real network database is usually slower.
Measured (`CatalogBench`, one run):

| d departments | m doctors | Map lookup per change | database query per change | memory of the Map |
|---|---|---|---|---|
| 10 | 500 | 108 ns | 270,946 ns | about 0 KB (below noise) |
| 20 | 2,000 | 143 ns | 391,423 ns | 4.5 KB |
| 50 | 10,000 | 17 ns | 232,755 ns | 51.8 KB |
| 100 | 50,000 | 17 ns | 468,538 ns | 196.4 KB |

- **Time per department change:** Map approach `O(1)` expected (hash lookup, then the list is copied into the combo box: `O(r)` where `r = m/d` doctors of that department).
  Database approach: `O(m)` for an unindexed scan, or `O(log m + r)` with an index on `department`, **plus** one network round trip `L` each time. Here the database path is thousands of times slower, and it must run in a `SwingWorker`, otherwise the window freezes while it waits.
- **Memory:** Map approach `O(d + m)` (d keys and m references to doctor names), small here (196 KB for 50,000 doctors). Database approach: only the current list is held, `O(r)`, about `m/d` names.
- **Trade-off:** the Map can be stale if a doctor is added during the day (add a Refresh button or a time limit); the database approach is always current but costs latency on every change. For a few hundred doctors the Map is the better choice; for millions of rows the query (with an index and paging) is.
