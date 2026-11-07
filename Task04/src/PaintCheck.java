import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Label;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Verifies the TODO 1 and TODO 2 code WITHOUT a desktop.
 * A real Frame cannot be created on a headless machine, so the harness allocates a DrawPad object without running
 * its constructor (sun.misc.Unsafe), then drives the program's own listener and paint() method with synthetic events:
 *   - TODO 1: the real MouseAdapter subclass (DrawPad$1) receives 3 MOUSE_CLICKED events;
 *   - TODO 2: the real paint(Graphics) is called with a Graphics from an off-screen image.
 * "Minimise and restore" is modelled as: the old pixels are thrown away and the system calls paint() again.
 * This is evidence of the logic; for the submission also take real window screenshots on your own computer.
 */
public class PaintCheck {
    static int passed, failed;
    static void check(String name, boolean ok) { System.out.println((ok ? "PASS " : "FAIL ") + name); if (ok) passed++; else failed++; }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        Field uf = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        uf.setAccessible(true);
        sun.misc.Unsafe unsafe = (sun.misc.Unsafe) uf.get(null);

        DrawPad pad = (DrawPad) unsafe.allocateInstance(DrawPad.class);
        Field pointsF = DrawPad.class.getDeclaredField("points"); pointsF.setAccessible(true);
        Field statusF = DrawPad.class.getDeclaredField("status"); statusF.setAccessible(true);
        pointsF.set(pad, new java.util.ArrayList<Point>());
        Label label = (Label) unsafe.allocateInstance(Label.class);
        label.setText("Clicks: 0");
        statusF.set(pad, label);

        Class<?> listenerClass = Class.forName("DrawPad$1");
        Constructor<?> ctor = listenerClass.getDeclaredConstructor(DrawPad.class);
        ctor.setAccessible(true);
        MouseAdapter listener = (MouseAdapter) ctor.newInstance(pad);

        check("T1 listener class overrides mouseClicked (name spelled correctly)", listenerClass.getDeclaredMethod("mouseClicked", MouseEvent.class) != null);
        JPanel source = new JPanel();
        int[][] clicks = {{60, 70}, {200, 120}, {310, 210}};
        for (int[] c : clicks) listener.mouseClicked(new MouseEvent(source, MouseEvent.MOUSE_CLICKED, 0L, 0, c[0], c[1], 1, false));
        List<Point> points = (List<Point>) pointsF.get(pad);
        check("T2 three clicks stored three points", points.size() == 3);
        check("T3 label shows 'Clicks: 3'", label.getText().equals("Clicks: 3"));

        new File("output").mkdirs();
        BufferedImage before = draw(pad);
        ImageIO.write(before, "png", new File("output/DrawPad_before_minimise.png"));
        BufferedImage afterRestore = draw(pad);            // new pixels: the restore asks paint() to draw everything again
        ImageIO.write(afterRestore, "png", new File("output/DrawPad_after_restore.png"));
        boolean dotsBefore = dark(before, 60, 70) && dark(before, 200, 120) && dark(before, 310, 210);
        boolean dotsAfter = dark(afterRestore, 60, 70) && dark(afterRestore, 200, 120) && dark(afterRestore, 310, 210);
        check("T4 dots are centred on the click points before minimising", dotsBefore);
        check("T5 dots are still there after minimise/restore (paint redraws stored points)", dotsAfter);
        check("T6 area away from the clicks stays blank", !dark(afterRestore, 150, 250));

        // The defective version draws with getGraphics() only: nothing is stored, so a restore shows an empty window.
        BufferedImage buggyRestore = blank();
        ImageIO.write(buggyRestore, "png", new File("output/DrawPadBuggy_after_restore_model.png"));
        check("T7 model of the original program after restore is empty (nothing stored to redraw)", !dark(buggyRestore, 60, 70));

        System.out.println(passed + " passed, " + failed + " failed");
        if (failed > 0) System.exit(1);
    }

    static BufferedImage blank() {
        BufferedImage img = new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics(); g.setColor(Color.WHITE); g.fillRect(0, 0, 400, 300); g.dispose();
        return img;
    }

    static BufferedImage draw(DrawPad pad) {
        BufferedImage img = blank();
        Graphics2D g = img.createGraphics();
        g.setColor(Color.BLACK);
        pad.paint(g);                                       // the program's own paint(Graphics)
        g.dispose();
        return img;
    }

    static boolean dark(BufferedImage img, int x, int y) { return (img.getRGB(x, y) & 0xFFFFFF) < 0x404040; }
}
