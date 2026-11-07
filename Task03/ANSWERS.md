# Task 3 - From Sketch to Layout (K4 - Analyse, 9 marks)

**Student:** Syed Aakif Hussain S A | **Register No.:** 192525472

Files: `src/LibraryPanel.java` (the screen), `src/LibraryFrame.java` (window that hosts it), `src/LibraryPanelGridBag.java`, `src/LibraryPanelAbsolute.java` (alternatives),
`src/ResizeCheck.java` (lays out and paints the screen at three sizes), `src/LayoutBench.java`.
Evidence: `output/Library_480x360.png`, `Library_800x600.png`, `Library_1100x750.png`, `Library_absolute_1100x750.png`, `Library_gridbag_1100x750.png`,
`output/ResizeCheck_output.txt`, `output/LayoutBench_output.txt`.

## Task 3.1 - Choose the layout managers (3 marks)

Resizing requirement: the **results area grows**; the search row, button row, title and status keep their size.
`BorderLayout` gives the extra space only to the CENTER region, while NORTH/SOUTH keep their preferred height. `FlowLayout` never stretches its components.

| Container | Layout manager | Justification for the resizing requirement |
|---|---|---|
| Root panel | **BorderLayout** | NORTH = top block, CENTER = results, SOUTH = bottom block. All extra width and height go to CENTER. |
| `top` (title + search row) | **BorderLayout** | NORTH = title, SOUTH = search row: they stack at their preferred heights and stretch only in width. |
| `searchRow` | **FlowLayout (LEFT)** | Text field, `Choice`/combo and Search button keep their preferred sizes in one row and are not stretched. |
| Results area | `JScrollPane` around `JTextArea` in the root CENTER | This is the only component that grows; the scroll bars appear when the text is larger than the area. |
| `bottom` (buttons + status) | **BorderLayout** | CENTER = button row, SOUTH = status message; fixed height, full width. |
| `buttonRow` | **FlowLayout (CENTER)** | Prev, Next and Reserve keep their size and stay centred when the window is resized. |

Why not the others: `GridLayout` makes every cell the same size, so the buttons and the text field would stretch;
`CardLayout` shows one component at a time (it suits switching between screens, as in Task 6), not one screen with several regions.

## Task 3.2 - Nested container structure (2 marks)

```
LibraryPanel (JPanel, BorderLayout)
|
+-- NORTH  : top (JPanel, BorderLayout)
|     +-- NORTH : title  (JLabel "Campus Library Search")
|     +-- SOUTH : searchRow (JPanel, FlowLayout LEFT)
|                  +-- JLabel "Search:"  +-- JTextField  +-- JComboBox category  +-- JButton Search
|
+-- CENTER : JScrollPane  -->  JTextArea results                 <-- the only region that grows
|
+-- SOUTH  : bottom (JPanel, BorderLayout)
      +-- CENTER : buttonRow (JPanel, FlowLayout CENTER)
      |             +-- JButton Prev   +-- JButton Next   +-- JButton Reserve
      +-- SOUTH  : status (JLabel "Ready")
```
Five panels (`LibraryPanel`, `top`, `searchRow`, `bottom`, `buttonRow`), two layout manager types (`BorderLayout` x3, `FlowLayout` x2).

## Task 3.3 - Implementation (3 marks)

`src/LibraryPanel.java` builds exactly the tree above with Swing components; `src/LibraryFrame.java` puts it in a `JFrame`
(start size 600 x 420, minimum 460 x 300 because a `FlowLayout` row wraps below about 430 px).
Run on a desktop with `java -cp out LibraryFrame` and drag the window edges.

## Task 3.4 - Resizing behaviour (1 mark)

The real components were laid out and painted at three window sizes (`ResizeCheck`, off-screen because this machine has no desktop). Measured region sizes:

| Region | 480 x 360 | 800 x 600 | 1100 x 750 | Behaved as designed? |
|---|---|---|---|---|
| Title | 480 x 39 | 800 x 39 | 1100 x 39 | Height fixed, width follows the window. Yes |
| Search row | 480 x 37 | 800 x 37 | 1100 x 37 | Height fixed. Yes |
| Search button | 75 x 25 at x=351 | 75 x 25 at x=351 | 75 x 25 at x=351 | Keeps size and position (FlowLayout LEFT). Yes |
| **Results** | **480 x 226** | **800 x 466** | **1100 x 616** | **Grows in both directions. Yes** |
| Button row | 480 x 37 | 800 x 37 | 1100 x 37 | Height fixed; the three buttons stay centred. Yes |
| Status | 480 x 21 | 800 x 21 | 1100 x 21 | Height fixed. Yes |

Screenshots: `output/Library_480x360.png`, `output/Library_800x600.png`, `output/Library_1100x750.png` (images of the real Swing components painted into an image; for the final submission also take three screenshots of the running window on your computer).
The only region whose size changes with the window (besides the full-width bars) is the results area, as required.

## Alternative Concept and Complexity Analysis

**Alternative 1 - `GridBagLayout`** (`src/LibraryPanelGridBag.java`): one panel, no nested panels. Each component gets `gridx/gridy`,
`fill` and `weightx/weighty`; the results area has `weightx = weighty = 1` and `fill = BOTH`, the others weight 0. It resizes correctly
(`output/Library_gridbag_1100x750.png`: results grow, the text field also stretches because it has `weightx = 1`), but the constraints are verbose and easy to get wrong.

**Alternative 2 - absolute positioning** (`src/LibraryPanelAbsolute.java`): `setLayout(null)` and `setBounds(x, y, w, h)` for every component.
`output/Library_absolute_1100x750.png` shows the failure: at 1100 x 750 nothing moved or grew, the content stays in a 600 x 375 corner. It also breaks with a different font size, screen DPI or language.

Measured with `LayoutBench` (one full relayout of the whole component tree; one run):

| Design | Extra JPanel containers | Components | Relayout time |
|---|---|---|---|
| Nested BorderLayout + FlowLayout | 5 | 25 | 41 us |
| GridBagLayout | 1 | 18 | 40 us |
| Absolute (`null` layout) | 1 | 20 | 8 us |

The three screens are not identical (the alternatives are simplified: fewer buttons in the GridBag version), so the component counts are only roughly comparable.

- **Computation time:** laying out the screen is `O(n)` in the number of components for all designs, since each component is visited once. Nested `Border/Flow`: the sum over the 5 small containers is `O(n)`. `GridBagLayout`: also `O(n)` per container but with a heavier constant (it computes row and column sizes and weights: about `O(n * (rows + columns))`). Absolute positioning does almost no work (`O(n)` only to copy the stored bounds), which is why it is the fastest in the table, and why it does not adapt.
- **Space (container objects):** nested design 5 panels + 5 layout manager objects; GridBag 1 panel + 1 layout manager + 1 constraints object per component; absolute 1 panel and no layout manager. The differences are a few hundred bytes, irrelevant next to the cost of maintenance.
- **Conclusion:** nested `BorderLayout`/`FlowLayout` is the clearest way to express "this region grows, the others do not", while the layout cost of all three is negligible for a screen of 25 components. Use `GridBagLayout` for form-like grids, and avoid absolute positioning for resizable windows.
