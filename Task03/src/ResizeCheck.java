import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * Resizing test. The real Swing components are laid out and painted at three window sizes into an
 * off-screen image (this machine has no desktop). Region bounds are printed so each region's behaviour
 * can be checked against the design: only the results area may grow.
 */
public class ResizeCheck {
    static void layoutAll(Component c) {
        if (c instanceof Container k) { k.doLayout(); for (Component child : k.getComponents()) layoutAll(child); }
    }

    static BufferedImage render(JComponent panel, int w, int h) {
        panel.setSize(w, h);
        layoutAll(panel);
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        panel.paint(g);
        g.dispose();
        return img;
    }

    static String bounds(String name, Component c) {
        Rectangle r = c.getBounds();
        return String.format("   %-12s x=%-4d y=%-4d width=%-4d height=%-4d", name, r.x, r.y, r.width, r.height);
    }

    public static void main(String[] args) throws Exception {
        new File("output").mkdirs();
        int[][] sizes = {{480, 360}, {800, 600}, {1100, 750}};
        SwingUtilities.invokeAndWait(() -> {
            try {
                for (int[] s : sizes) {
                    LibraryPanel p = new LibraryPanel();
                    BufferedImage img = render(p, s[0], s[1]);
                    String file = "output/Library_" + s[0] + "x" + s[1] + ".png";
                    ImageIO.write(img, "png", new File(file));
                    System.out.println("Nested BorderLayout/FlowLayout design at " + s[0] + " x " + s[1] + "  -> " + file);
                    System.out.println(bounds("title", p.title));
                    System.out.println(bounds("searchRow", p.searchRow));
                    System.out.println(bounds("results", p.resultsPane));
                    System.out.println(bounds("buttonRow", p.buttonRow));
                    System.out.println(bounds("status", p.status));
                    System.out.println(bounds("Search btn", p.search));
                }
                LibraryPanelAbsolute abs = new LibraryPanelAbsolute();
                ImageIO.write(render(abs, 1100, 750), "png", new File("output/Library_absolute_1100x750.png"));
                System.out.println("Absolute-positioning alternative at 1100 x 750 -> output/Library_absolute_1100x750.png (content stays at 600 x 375)");
                LibraryPanelGridBag gb = new LibraryPanelGridBag();
                ImageIO.write(render(gb, 1100, 750), "png", new File("output/Library_gridbag_1100x750.png"));
                System.out.println("GridBagLayout alternative at 1100 x 750 -> output/Library_gridbag_1100x750.png");
            } catch (Exception e) { throw new RuntimeException(e); }
        });
    }
}
