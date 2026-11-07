import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Time per repaint for n dots: redraw every stored point (O(n)) versus copy one off-screen image (O(w*h), no n).
 * Memory: n Point objects (O(n)) versus width x height pixels (fixed). Measured on off-screen images.
 */
public class PaintBench {
    static volatile int sink;

    public static void main(String[] args) {
        int w = 400, h = 300;
        int[] ns = {100, 1_000, 10_000, 100_000};
        System.out.printf("%-9s %-24s %-24s %-18s %-18s%n", "n clicks", "redraw points (us)", "copy BufferedImage (us)", "points (KB)", "image (KB)");
        BufferedImage canvas = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        BufferedImage target = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int n : ns) {
            long before = used();
            List<Point> points = new ArrayList<>();
            for (int i = 0; i < n; i++) points.add(new Point((i * 37) % w, (i * 53) % h));
            long pointBytes = used() - before;
            Graphics2D g = target.createGraphics();
            g.setColor(Color.BLACK);
            int rounds = Math.max(5, 200_000 / n);
            for (int i = 0; i < rounds / 2 + 1; i++) { for (Point p : points) g.fillOval(p.x - 3, p.y - 3, 6, 6); }   // warm-up
            long t = System.nanoTime();
            for (int i = 0; i < rounds; i++) for (Point p : points) g.fillOval(p.x - 3, p.y - 3, 6, 6);
            double pointsUs = (System.nanoTime() - t) / (double) rounds / 1000.0;
            for (int i = 0; i < 200; i++) g.drawImage(canvas, 0, 0, null);
            t = System.nanoTime();
            for (int i = 0; i < 2000; i++) g.drawImage(canvas, 0, 0, null);
            double imageUs = (System.nanoTime() - t) / 2000.0 / 1000.0;
            g.dispose();
            sink += points.size();
            System.out.printf("%-9d %-24.1f %-24.1f %-18d %-18d%n", n, pointsUs, imageUs, Math.max(pointBytes, 0) / 1024, w * h * 4 / 1024);
        }
        System.out.println("Points: repaint O(n), memory O(n).  Image: repaint O(w*h) independent of n, memory O(w*h) fixed (about 469 KB here).");
    }

    static long used() { System.gc(); Runtime r = Runtime.getRuntime(); return r.totalMemory() - r.freeMemory(); }
}
