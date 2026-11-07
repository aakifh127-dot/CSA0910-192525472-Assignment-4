import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/** Alternative design: absolute positioning (setLayout(null) + setBounds). Nothing moves or grows on resize. */
public class LibraryPanelAbsolute extends JPanel {
    public LibraryPanelAbsolute() {
        super(null);
        JLabel title = new JLabel("Campus Library Search", JLabel.CENTER); title.setBounds(0, 0, 600, 30);
        JTextField q = new JTextField(); q.setBounds(10, 40, 260, 28);
        JComboBox<String> cat = new JComboBox<>(new String[]{"Title", "Author", "Subject", "ISBN"}); cat.setBounds(280, 40, 100, 28);
        JButton search = new JButton("Search"); search.setBounds(390, 40, 90, 28);
        JTextArea area = new JTextArea("1. Programming in Java\n2. Data Structures in C\n");
        JScrollPane pane = new JScrollPane(area); pane.setBounds(10, 80, 580, 230);
        JButton prev = new JButton("Prev"), next = new JButton("Next"), reserve = new JButton("Reserve");
        prev.setBounds(150, 320, 90, 28); next.setBounds(250, 320, 90, 28); reserve.setBounds(350, 320, 90, 28);
        JLabel status = new JLabel("Ready"); status.setBounds(10, 355, 300, 20);
        for (java.awt.Component c : new java.awt.Component[]{title, q, cat, search, pane, prev, next, reserve, status}) add(c);
    }
}
