# Task 4 - Event-Handling Forensics (K4 - Analyse, 7 marks)

**Student:** Syed Aakif Hussain S A | **Register No.:** 192525472

Files: `src/DrawPadBuggy.java` (the faulty program, as reported), `src/DrawPad.java` (repaired, with TODO 1 and TODO 2 completed),
`src/DrawPadBuffered.java` (alternative), `extras/DrawPadOverrideError.java` (Task 4.4), `src/PaintCheck.java`, `src/PaintBench.java`.
Evidence: `output/PaintCheck_output.txt` (7 PASS), `output/DrawPad_before_minimise.png`, `output/DrawPad_after_restore.png`,
`output/OverrideError_output.txt` (compiler message), `output/PaintBench_output.txt`.

## Task 4.1 - Locate the defect (2 marks)

1. **The click is never handled because the method name is wrong.** `MouseAdapter` declares `mouseClicked` (capital **C**).
   The program defines `mouse`**`c`**`licked`. That is a new, unrelated method, not an override. When the user clicks, AWT calls `mouseClicked` on the listener;
   the adapter's own empty `mouseClicked` runs, and the programmer's method is never called. The code compiles, so nothing warns about it.
2. **The label never changes because nothing updates it.** Even if the method were called, it does `count++` but never calls `status.setText(...)`.
   A `Label` shows the text it was given; changing an `int` field does not change the screen.
3. (Secondary) The dot is drawn with its corner, not its centre, at the click point (`fillOval(e.getX(), e.getY(), 6, 6)`).

## Task 4.2 - A failing interaction (2 marks)

Fix only defect 1 (correct the name) and run the program:

1. Click three times in the window: three dots appear.
2. **Minimise the window and restore it** (or drag another window over it, or resize it).
3. The dots are gone, and the label (if it was updated) still says `Clicks: 3` but there is nothing to see.

**Why:** the program draws with `getGraphics()` straight onto the screen. AWT uses passive rendering: whenever the window is uncovered, restored or resized,
the system discards the old pixels and calls `paint(Graphics)` again, and `paint` only draws what the component's own code knows how to draw (here: nothing but the label).
The dots were never stored anywhere, so `paint` cannot redraw them. The rule: **store the data, draw it in `paint()`, and call `repaint()` when the data changes.**
(`output/DrawPadBuggy_after_restore_model.png` is the empty window the original produces after a restore.)

## Task 4.3 - Repair (2 marks)

`src/DrawPad.java`:

```java
addMouseListener(new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {          // TODO 1: correct name, store, update label, request repaint
        points.add(e.getPoint());
        status.setText("Clicks: " + points.size());
        repaint();
    }
});

@Override
public void paint(Graphics g) {                       // TODO 2: redraw every stored point as a 6 x 6 circle centred on it
    for (Point p : points) g.fillOval(p.x - 3, p.y - 3, 6, 6);
}
```

Verification without a desktop (`PaintCheck`; a real `Frame` cannot be created on this headless machine, so the harness creates a `DrawPad` object
without running its constructor and drives the program's own listener and `paint()` with synthetic events). All 7 checks pass:
the listener class really contains `mouseClicked`; three clicks store three points; the label reads `Clicks: 3`; the dots are centred on the click points;
after a "restore" (fresh pixels, `paint()` called again) the dots are still there; the area away from the clicks stays blank; and the original program's restored window is empty.
`output/DrawPad_before_minimise.png` and `output/DrawPad_after_restore.png` are the images painted by `DrawPad.paint()` (client area only, without the title bar and label).
**For the submission:** run `java -cp out DrawPad` on your computer, click three times, and take a screenshot before and after minimising and restoring the window.

## Task 4.4 - Prevent recurrence (1 mark)

Adding `@Override` makes the compiler check that the method really overrides something. With the misspelled name the compiler stops with
(`output/OverrideError_output.txt`, from `extras/DrawPadOverrideError.java`):

```
extras/DrawPadOverrideError.java:16: error: method does not override or implement a method from a supertype
            @Override
            ^
1 error
```

The defect that went unnoticed at run time is therefore reported at compile time, with the exact line. (An IDE also underlines it.) Use `@Override` on every listener method and every `paint`.

## Alternative Concept and Complexity Analysis

**Alternative:** draw each dot once onto an off-screen `BufferedImage`; `paint()` only copies that image to the screen (`src/DrawPadBuffered.java`).

```java
// click:  Graphics2D g = canvas.createGraphics(); g.fillOval(x - 3, y - 3, 6, 6); g.dispose(); repaint();
// paint:  g.drawImage(canvas, 0, 0, null);        // one copy, whatever the number of clicks
```

Measured with `PaintBench` (off-screen target, 400 x 300 image; one run):

| n clicks | redraw all points | copy one image | memory: n `Point` objects | memory: image |
|---|---|---|---|---|
| 100 | 84 us | 56 us | about 0 KB | 468 KB |
| 1,000 | 766 us | 51 us | 8 KB | 468 KB |
| 10,000 | 7,948 us | 61 us | 289 KB | 468 KB |
| 100,000 | 88,128 us | 59 us | 2,443 KB | 468 KB |

- **Time per repaint:** stored points `O(n)` (every dot is drawn again; the table grows by 10x for each 10x of n). Image copy `O(width x height)`, **independent of n** (about 55 us here, because the image size is fixed).
  The image is faster once n exceeds roughly 70 dots on this machine.
- **Space:** points `O(n)` (about 25 bytes per `Point` here; 2.4 MB for 100,000); image `O(width x height)` = 400 x 300 x 4 bytes = 469 KB, fixed. The image uses more memory until about 19,000 points.
- **Trade-off:** the point list keeps the data (dots can be undone, moved, saved as coordinates, redrawn at any size); the image keeps only pixels (no undo of a single dot, a bigger window needs a bigger image, and the image grows with the window area even when empty). A real program often does both: keep the list as the model and cache the image as a speed-up.
