import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Label;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

/**
 * Alternative concept: each dot is drawn ONCE onto an off-screen image; paint() only copies that image.
 * Repaint cost no longer depends on the number of clicks, but the dots cannot be moved or undone individually.
 */
public class DrawPadBuffered extends Frame {
    private final BufferedImage canvas = new BufferedImage(400, 300, BufferedImage.TYPE_INT_ARGB);
    private final Label status = new Label("Clicks: 0");
    private int count;

    public DrawPadBuffered() {
        super("Draw Pad (buffered)");
        add(status, BorderLayout.SOUTH);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Graphics2D g = canvas.createGraphics();
                g.fillOval(e.getX() - 3, e.getY() - 3, 6, 6);
                g.dispose();
                status.setText("Clicks: " + (++count));
                repaint();
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) { dispose(); }
        });
        setSize(400, 300);
    }

    @Override
    public void paint(Graphics g) { g.drawImage(canvas, 0, 0, null); }

    public static void main(String[] args) { new DrawPadBuffered().setVisible(true); }
}
