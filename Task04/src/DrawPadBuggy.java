import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** The faulty program exactly as reported (imports added so it compiles). It compiles, but never reacts to a click. */
public class DrawPadBuggy extends Frame {
    int count = 0;
    Label status = new Label("Clicks: 0");

    public DrawPadBuggy() {
        add(status, BorderLayout.SOUTH);
        addMouseListener(new MouseAdapter() {
            public void mouseclicked(MouseEvent e) {          // DEFECT: lower-case c, so this is a NEW method, not an override
                count++;                                       // DEFECT: label is never updated here
                Graphics g = getGraphics();                    // DEFECT: drawing outside paint() is lost on the next repaint
                g.fillOval(e.getX(), e.getY(), 6, 6);
            }
        });
        setSize(400, 300);
        setVisible(true);
    }

    public static void main(String[] args) { new DrawPadBuggy(); }
}
