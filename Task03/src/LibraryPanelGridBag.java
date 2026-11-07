import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/** Alternative design: ONE GridBagLayout container, no nested panels (3 columns, 4 rows). */
public class LibraryPanelGridBag extends JPanel {
    public LibraryPanelGridBag() {
        super(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.gridx = 0; c.gridy = 0; c.gridwidth = 3; c.fill = GridBagConstraints.HORIZONTAL;
        add(new JLabel("Campus Library Search", JLabel.CENTER), c);
        c.gridy = 1; c.gridwidth = 1; c.weightx = 1;
        add(new JTextField(18), c);
        c.gridx = 1; c.weightx = 0;
        add(new JComboBox<>(new String[]{"Title", "Author", "Subject", "ISBN"}), c);
        c.gridx = 2;
        add(new JButton("Search"), c);
        c.gridx = 0; c.gridy = 2; c.gridwidth = 3; c.weightx = 1; c.weighty = 1; c.fill = GridBagConstraints.BOTH;
        JTextArea area = new JTextArea("1. Programming in Java\n2. Data Structures in C\n");
        add(new JScrollPane(area), c);
        c.gridy = 3; c.weighty = 0; c.fill = GridBagConstraints.NONE;
        add(new JButton("Prev"), c);        // simplified: buttons stacked in one cell row
        c.gridy = 4; c.fill = GridBagConstraints.HORIZONTAL;
        add(new JLabel("Ready"), c);
    }
}
