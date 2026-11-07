# Task 1 - GUI Design Review (K4 - Analyse, 6 marks)

**Student:** Syed Aakif Hussain S A | **Register No.:** 192525472

Files: `src/original/FeeWindowBad.txt` (the junior developer's code), `src/FeeService.java`, `src/FeeWindow.java`,
`src/FeeRepository.java`, `src/JdbcFeeRepository.java`, `src/InMemoryFeeRepository.java`, `src/FeeServiceTest.java`, `src/DispatchBench.java`.
Evidence: `output/FeeServiceTest_output.txt` (14 PASS), `output/DispatchBench_output.txt`.

## Task 1.1 - Diagnose the design (2 marks)

| # | Deficiency in the original | What the user experiences / what can go wrong |
|---|---|---|
| 1 | **Interface and logic are mixed.** One `actionPerformed` validates, opens a connection, writes SQL, waits and prints. | The payment rules cannot be tested without opening a window and a database; a change to one button risks all the others. |
| 2 | **`e.getActionCommand() == "Pay"` compares references, not text.** It works only while both strings happen to be the same interned literal. | If the command comes from `new String(...)`, `setActionCommand`, or a translated label, the test is false and a Pay click silently falls into the `else` branch and **wipes the form**. |
| 3 | **The `else` branch handles both Clear and Receipt.** | Clicking **Receipt** erases what the student typed, and no receipt is ever shown. The Receipt button has no feature behind it. |
| 4 | **No validation of the input.** `Double.parseDouble(amount.getText())` is called on raw text. | An empty or non-numeric amount throws `NumberFormatException` on the event thread: a stack trace in the console, nothing in the window. Zero, negative, `NaN` and absurd amounts are accepted; an empty register number is accepted. |
| 5 | **Database work and `Thread.sleep(3000)` run on the event-dispatch thread.** | The whole window freezes for 3 seconds or longer: it cannot repaint, move or close ("Not responding"). Impatient users click again and **pay twice**. |
| 6 | **SQL is built by string concatenation.** | SQL injection: a register number such as `1'); DROP TABLE fees;--` runs as SQL. Even an honest apostrophe in the text breaks the statement. |
| 7 | **The connection and statement are never closed.** | Every payment leaks a connection; after enough payments the database refuses new ones. |
| 8 | **Checked exceptions (`SQLException`, `InterruptedException`) are not handled**, and `actionPerformed` cannot declare them. | The code does not even compile as written; once wrapped in an empty `catch` the failure is invisible and the student does not know whether the money was recorded. |
| 9 | **Feedback goes to `System.out.println("Paid")`.** | A user of the window sees nothing: no confirmation, no error text, no receipt number. |
| 10 | **The window is never wired or closed.** No `addActionListener` calls, no layout/`add()` calls, no `setSize`/`setVisible`, no `WindowListener`; an AWT `Frame` ignores the close button by default. | The window would show nothing and the buttons do nothing; once shown it can only be closed by killing the program. |
| 11 | **Credentials and URL are constants used directly in the handler.** | Passwords sit in UI code; changing the database means editing the window class. |

## Task 1.2 - Repair the design (3 marks)

### Class structure

```
 +-------------------+  calls   +------------------+  uses   +-----------------------+
 | FeeWindow         |--------->| FeeService       |-------->| <<interface>>         |
 | (AWT Frame, view) |          | validation +     |         | FeeRepository         |
 | 3 small listeners |          | payment rules    |         |  save(reg, amount)    |
 | worker thread     |<---------| (no AWT code)    |         |  findLast(reg)        |
 +-------------------+  Receipt +------------------+         +-----------+-----------+
        |  shows text           throws FeeException                       |
        v                       (message safe for the user)    +---------+----------+
   status Label                                                | JdbcFeeRepository  |  PreparedStatement,
                                                               | InMemoryFeeRepository | try-with-resources
                                                               +--------------------+
```

### Rewritten event handling (from `src/FeeWindow.java`)

```java
pay.addActionListener(e -> doPay());                       // one small listener per button
clear.addActionListener(e -> { regNo.setText(""); amount.setText(""); show("Cleared"); });
receipt.addActionListener(e -> doReceipt());

private void doPay() {
    String reg = regNo.getText(), amt = amount.getText();   // read the fields on the event thread
    pay.setEnabled(false);                                  // no double payment
    show("Processing payment...");
    worker.submit(() -> {                                   // background thread: the window stays alive
        String message;
        try { message = "Paid: " + service.pay(reg, amt); }
        catch (FeeException ex) { message = ex.getMessage(); }   // every failure becomes readable text
        String finalMessage = message;
        EventQueue.invokeLater(() -> { show(finalMessage); pay.setEnabled(true); });   // back on the event thread
    });
}
```

How each deficiency is removed: (1) `FeeService` holds all rules; (2) separate listeners, no string comparison;
(3) Receipt has its own handler and lookup; (4) `FeeService.checkAmount/checkRegNo` reject empty, non-numeric, zero, negative, `NaN`
and too-large values; (5) `ExecutorService` worker + `EventQueue.invokeLater`; (6) `PreparedStatement` in `JdbcFeeRepository`
(and the register number is also validated before it gets there); (7) try-with-resources closes connection, statement and result set;
(8) `SQLException` is converted into a `FeeException` with a user-safe message; (9) the status label shows every outcome;
(10) listeners are registered, components added, `WindowAdapter` closes the window and shuts the worker down.

### Evidence that the logic works (`output/FeeServiceTest_output.txt`)

14 of 14 tests pass: valid payment, empty/blank register number, empty/non-numeric/negative/zero/NaN/too-large amounts,
SQL-injection text rejected before storage, nothing stored by rejected calls, receipt lookup, receipt for an unknown student,
the most recent payment returned, and a storage failure converted to a readable message.
`FeeWindow` compiles but needs a desktop to open (`java -cp out FeeWindow`); on that machine take a screenshot of one successful payment and one error message.

## Task 1.3 - Design defence (1 mark, 88 words)

The window now only reads text fields and shows messages. Every rule lives in FeeService, which has no AWT code, so it is tested with plain method calls: FeeServiceTest runs 14 cases in milliseconds. Storage sits behind the FeeRepository interface, so tests use an in-memory copy and production uses JDBC without touching the rules. Each button has its own small listener, so changing Clear cannot break Pay. Slow work runs on a worker thread, so the window never freezes. A new rule means one edit in one class.

## Alternative Concept and Complexity Analysis

**Alternative:** instead of one listener with an `if-else` chain on the action command, give every button its own listener object
(or one Swing `Action` per button). This is exactly what the repaired `FeeWindow` does:

```java
// original: one listener, chain of string comparisons, k = number of buttons
public void actionPerformed(ActionEvent e) {
    String c = e.getActionCommand();
    if (c.equals("Pay")) ... else if (c.equals("Clear")) ... else if (c.equals("Receipt")) ...
}
// alternative: the button that was clicked already knows its handler
pay.addActionListener(e -> doPay());
// or, in Swing, one Action per command:  JButton pay = new JButton(new PayAction(service));
```

**Time to dispatch one event** (worst case: the event comes from the last of k buttons). Measured with `DispatchBench` (nanoseconds per dispatch):

| k buttons | if-else chain | listener per button | HashMap of handlers | memory of k listener objects |
|---|---|---|---|---|
| 2 | 10.8 | 16.1 | 8.6 | below measurement noise |
| 8 | 17.4 | 6.7 | 7.9 | below measurement noise |
| 32 | 73.6 | 6.7 | 8.6 | 2,224 bytes |
| 128 | 164.6 | 6.6 | 8.5 | 5,920 bytes |
| 512 | 1084.0 | 8.8 | 8.5 | 36,784 bytes |

(figures from one run; they vary a little from run to run, the trend does not.)

- **If-else chain:** `O(k)` comparisons in the worst case (about `k/2` on average); each `String.equals` may also compare characters, so the cost per comparison depends on the command length. The time grows almost linearly with k in the table.
- **One listener per button:** `O(1)`. The source (the button) holds its own listener and calls it directly; the time stays flat (about 7 ns).
- **HashMap of handlers** (a middle design with one listener): `O(1)` expected.
- **Space:** the chain uses one listener object but must keep `k` command strings; the per-button design uses `k` listener objects of constant size (about 70 bytes each in the table). Both are `O(k)`; the difference is only a constant factor.

**Conclusion:** the speed difference is a few nanoseconds for a real window with 3 buttons and is not the reason to choose the alternative.
The real reasons are separation (a handler can be changed or tested alone), no string comparison bug (deficiency 2), and no accidental "else" branch (deficiency 3).
