import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Task 4.4: the buggy program with @Override added and the method still misspelled. javac must reject it. */
public class DrawPadOverrideError extends Frame {
    int count = 0;
    Label status = new Label("Clicks: 0");

    public DrawPadOverrideError() {
        add(status, BorderLayout.SOUTH);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseclicked(MouseEvent e) {
                count++;
                Graphics g = getGraphics();
                g.fillOval(e.getX(), e.getY(), 6, 6);
            }
        });
        setSize(400, 300);
        setVisible(true);
    }
}
