import java.awt.Component;
import java.awt.Container;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/** Layout computation time (one full relayout of the whole tree) and number of containers for each design. */
public class LayoutBench {
    static int containers, components;

    static void count(Component c) {
        components++;
        if (c instanceof javax.swing.JPanel) containers++;
        if (c instanceof Container k) { for (Component child : k.getComponents()) count(child); }
    }

    static double timeLayout(JComponent p, int rounds) {
        int[] w = {480, 800, 1100}, h = {360, 600, 750};
        for (int i = 0; i < rounds / 2; i++) { p.setSize(w[i % 3], h[i % 3]); ResizeCheck.layoutAll(p); }   // warm-up
        long t = System.nanoTime();
        for (int i = 0; i < rounds; i++) { p.setSize(w[i % 3], h[i % 3]); ResizeCheck.layoutAll(p); }
        return (System.nanoTime() - t) / (double) rounds / 1000.0;
    }

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JComponent[] designs = {new LibraryPanel(), new LibraryPanelGridBag(), new LibraryPanelAbsolute()};
            String[] names = {"Nested Border/Flow", "GridBagLayout", "Absolute (null)"};
            System.out.printf("%-22s %-14s %-12s %-22s%n", "Design", "JPanels", "Components", "Relayout time (us)");
            for (int i = 0; i < designs.length; i++) {
                containers = 0; components = 0;
                count(designs[i]);
                System.out.printf("%-22s %-14d %-12d %-22.1f%n", names[i], containers, components, timeLayout(designs[i], 3000));
            }
            System.out.println("All three are O(n) in the number of components for one relayout; they differ in constant factor, in");
            System.out.println("container objects (space) and, for the absolute design, in not reacting to a resize at all.");
        });
    }
}
