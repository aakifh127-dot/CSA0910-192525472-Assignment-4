import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Campus Library Search screen, nested BorderLayout + FlowLayout design.
 *
 * root (BorderLayout)
 *  |- NORTH  top (BorderLayout)
 *  |    |- NORTH  title label
 *  |    '- SOUTH  searchRow (FlowLayout): JTextField, JComboBox, Search button
 *  |- CENTER JScrollPane(JTextArea results)      <- the only region that grows
 *  '- SOUTH  bottom (BorderLayout)
 *       |- CENTER buttonRow (FlowLayout): Prev, Next, Reserve
 *       '- SOUTH  status label
 */
public class LibraryPanel extends JPanel {
    final JLabel title = new JLabel("Campus Library Search", JLabel.CENTER);
    final JPanel top = new JPanel(new BorderLayout());
    final JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
    final JTextField query = new JTextField(18);
    final JComboBox<String> category = new JComboBox<>(new String[]{"Title", "Author", "Subject", "ISBN"});
    final JButton search = new JButton("Search");
    final JTextArea results = new JTextArea();
    final JScrollPane resultsPane = new JScrollPane(results);
    final JPanel bottom = new JPanel(new BorderLayout());
    final JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
    final JButton prev = new JButton("Prev");
    final JButton next = new JButton("Next");
    final JButton reserve = new JButton("Reserve");
    final JLabel status = new JLabel("Ready");

    public LibraryPanel() {
        super(new BorderLayout());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        title.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        searchRow.add(new JLabel("Search:")); searchRow.add(query); searchRow.add(category); searchRow.add(search);
        top.add(title, BorderLayout.NORTH);
        top.add(searchRow, BorderLayout.SOUTH);

        results.setEditable(false);
        results.setText("1. Programming in Java - Herbert Schildt\n2. Data Structures in C - Reema Thareja\n3. Database System Concepts - Silberschatz\n4. Computer Networks - Tanenbaum\n5. Operating System Concepts - Galvin\n");

        buttonRow.add(prev); buttonRow.add(next); buttonRow.add(reserve);
        status.setBorder(BorderFactory.createEmptyBorder(2, 8, 4, 8));
        bottom.add(buttonRow, BorderLayout.CENTER);
        bottom.add(status, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(resultsPane, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }
}
