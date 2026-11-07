import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/** Window that hosts the panel. Run this on a desktop and drag the window edges to see the resizing. */
public class LibraryFrame extends JFrame {
    public LibraryFrame() {
        super("Campus Library");
        setContentPane(new LibraryPanel());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 420);
        setMinimumSize(new java.awt.Dimension(460, 300));   // FlowLayout rows wrap below this width
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryFrame().setVisible(true));
    }
}
