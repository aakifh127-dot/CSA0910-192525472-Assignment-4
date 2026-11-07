# Task 5 - Same Screen, Different Event-Handling Styles (K5 - Evaluate, 7 marks)

**Student:** Syed Aakif Hussain S A | **Register No.:** 192525472

Files: `src/ConverterA.java` .. `ConverterD.java` (task 5.1), `src/ConverterA2.java` .. `ConverterD2.java` (task 5.2),
`src/Converter.java` (Complete the Code, lambda version), `src/ConverterShared.java` (alternative), `src/Rates.java`,
`src/RatesTest.java`, `src/ConverterCheck.java`, `src/TriggerCount.java`, `ChangeEffort.sh`.
Evidence: `output/RatesTest_output.txt` (8 PASS), `output/ConverterCheck_output.txt` (6 PASS), `output/TriggerCount_output.txt`, `output/ChangeEffort_output.txt`,
`output/Converter_valid_conversion.png`, `output/Converter_invalid_amount.png`.

## Task 5.1 - Four implementations (3 marks)

All four use the same window (INR label, amount field, USD/EUR choice, Convert button, result label) and the same rule `Rates.convert` (amount x 0.012 for USD, x 0.011 for EUR, two decimals, "Invalid amount" for bad input).
Window closing is handled in each by a `WindowAdapter` that overrides `windowClosing` (`DO_NOTHING_ON_CLOSE`, then `dispose()` and `System.exit(0)`).

| Style | File | How the Convert action is attached |
|---|---|---|
| (a) frame implements `ActionListener` | `ConverterA` | `class ConverterA extends JFrame implements ActionListener`; `convertButton.addActionListener(this)`; method `actionPerformed(ActionEvent)` |
| (b) named inner class | `ConverterB` | `private class ConvertHandler implements ActionListener {...}`; `addActionListener(new ConvertHandler())` |
| (c) anonymous inner class | `ConverterC` | `addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) {...} })` |
| (d) lambda expression | `ConverterD` | `addActionListener(e -> result.setText(Rates.convert(...)))` |

Verification: all files compile; `RatesTest` checks the conversion rule (8 PASS). The windows themselves need a desktop (`java -cp out ConverterA`).

## Task 5.2 - Change the requirement (2 marks)

New requirement: Enter in the amount field must also convert, and the result must also appear in a dialog box (`JOptionPane.showMessageDialog`).
`ConverterA2..D2` are the modified versions. The effort was **measured** by comparing each v1 file with its v2 file (class name and title changes ignored; `ChangeEffort.sh`):

| Style | Lines added | Lines removed | Total changed | What had to change |
|---|---|---|---|---|
| (a) frame implements `ActionListener` | 5 | 1 | **6** | One line `amount.addActionListener(this)`: the frame is already the listener. Dialog: 2 lines in `actionPerformed`, plus the import. |
| (b) named inner class | 5 | 1 | **6** | Register a second `ConvertHandler` on the field; dialog code inside the class; it must write `ConverterB2.this` to name the outer frame as the dialog parent. |
| (c) anonymous inner class | 8 | 3 | **11** | The listener was written inline inside `addActionListener`, so it had to be pulled out into a variable `handler` to register it twice. |
| (d) lambda | 9 | 1 | **10** | Same refactor: the one-line lambda became a block lambda stored in a variable (and an `ActionListener` import). |

Each count includes the new import lines. Observation: styles (a) and (b) changed least because the handler was already a named, reusable thing; (c) and (d) were the most compact to write at the start, but anything that has to be shared by two sources first needs to be given a name.

## Task 5.3 - Compare the styles (2 marks)

| Criterion | (a) frame is the listener | (b) named inner class | (c) anonymous inner class | (d) lambda |
|---|---|---|---|---|
| Readability | Poor when there are several buttons: one `actionPerformed` must find out which source fired. Fine for one button. | Good: the handler has a name and its own place. More lines. | Handler sits next to the registration (good locality) but the constructor becomes long and nested. | Best for one short action (one line). A long lambda body is unreadable. |
| Access to the frame's fields | Direct (it is the frame). | Direct (inner class sees the outer fields). | Direct (outer fields; local variables must be effectively final). | Direct (`this` is the frame, unlike in an anonymous class). |
| Reuse of the handler | Whole frame is one handler for everything. | Easy: create it wherever needed, can be tested and given a constructor. | Not reusable without a variable. | Reusable only if stored in a variable or turned into a method reference. |
| Ease of change | Every new button edits the same method (risk to others, a growing `if` chain). | Easy: edit or add one class. | Moderate (see 5.2: needs refactor to share). | Easy for small changes, same refactor as (c) to share. |

**Choice for a large application:** a small **lambda (or method reference) that only calls a named method of a separate controller/presenter class**, and a **named class or a Swing `Action`** when the handler has its own state or is used from several places (button, menu item, key).
Reasons: the window class stays a view, the real work can be unit-tested without a window (as `FeeService` in Task 1), several triggers share one definition, and the registration line stays readable. I would not make the frame itself the listener (a) in a large program because it becomes a single class that every event passes through.

## Complete the Code (lambda version)

`src/Converter.java`: the skeleton is unchanged except for the two TODO blocks.

```java
// TODO 1: ONE lambda listener for the Convert button and the Enter key in the amount field
ActionListener convertAction = e -> convert();
convertButton.addActionListener(convertAction);
amount.addActionListener(convertAction);          // Enter in a JTextField fires an ActionEvent

// TODO 2
private void convert() {
    try {
        double value = Double.parseDouble(amount.getText().trim());
        if (Double.isNaN(value) || Double.isInfinite(value) || value < 0) { result.setText("Invalid amount"); return; }
        String currency = (String) target.getSelectedItem();
        result.setText(String.format("%.2f %s", value * rate(currency), currency));
    } catch (NumberFormatException ex) { result.setText("Invalid amount"); }
}
```

Expected output compared with the actual output (`output/ConverterCheck_output.txt`, the program's own private `convert()` run on real Swing components):

| Input | Expected | Actual |
|---|---|---|
| 1000, USD | 12.00 USD | 12.00 USD |
| 1000, EUR | 11.00 EUR | 11.00 EUR |
| 250.5, USD | 3.01 USD | 3.01 USD |
| abc, USD | Invalid amount | Invalid amount |
| (empty), EUR | Invalid amount | Invalid amount |
| -5, USD | Invalid amount | Invalid amount |

`output/Converter_valid_conversion.png` and `output/Converter_invalid_amount.png` show the converter components after the program's `convert()` ran
(rendered off-screen, because this machine cannot open a window). The `ConverterCheck` harness creates the object without its constructor, so the lambda wiring of TODO 1
is verified by the compiler and by reading, not by a click. **For the submission, run `java -cp out Converter` on your computer and take a screenshot of a valid conversion and of an invalid amount (also test pressing Enter).**

## Alternative Concept and Complexity Analysis

**Alternative:** a method reference (`convertButton.addActionListener(this::convert)` with `void convert(ActionEvent e)`), or **one shared `Action` object**
used by the button, a menu item and a keyboard shortcut (`src/ConverterShared.java`):

```java
private final AbstractAction convertAction = new AbstractAction("Convert") {
    public void actionPerformed(ActionEvent e) { result.setText(Rates.convert(amount.getText(), (String) target.getSelectedItem())); }
};
add(new JButton(convertAction));            // button
menu.add(new JMenuItem(convertAction));     // menu item
amount.setAction(convertAction);            // Enter key
convertAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control ENTER"));
```

Measured with `TriggerCount` (distinct handler objects needed for three triggers):

| Approach | Distinct handler objects for 3 triggers | Work to add a 4th trigger |
|---|---|---|
| A separate anonymous listener per trigger | **3** | write one more listener (about 3 lines) |
| One lambda stored in a variable | **1** | one `addActionListener(handler)` line |
| One shared `Action` | **1** | one line, `new JButton(action)`; text, enabled state and shortcut come with it |

- **Space:** a listener per trigger costs `O(t)` objects for `t` triggers; a shared handler or `Action` costs `O(1)` handler objects (each trigger still stores one reference, so `O(t)` references). A method reference creates one small object, like a lambda.
- **Work to add a trigger:** `O(1)` lines for the shared forms against `O(t)` code duplicated across the other styles. Styles (a)-(d) as written in 5.1 each create one listener per registration, so with two triggers they create two (style (b) in `ConverterB2` made two `ConvertHandler` objects) unless the handler is stored in a variable, as `ConverterC2/D2` do.
- **Extra benefit of `Action`:** disabling it (`setEnabled(false)`) disables the button, menu item and shortcut together. Event dispatch time is the same `O(1)` per event for every style.
