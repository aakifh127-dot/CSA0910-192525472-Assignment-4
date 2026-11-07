import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Outpatient appointment desk (view only). Every handler prints one trace line so the order of calls
 * from window opening to closing can be compared with the table in ANSWERS.md.
 */
public class AppointmentDesk extends JFrame {
    private final DoctorCatalog catalog;
    private final AppointmentService service;
    private final JTextField patientId = new JTextField(10);
    private final JComboBox<String> department = new JComboBox<>();
    private final JComboBox<String> doctor = new JComboBox<>();
    private final JComboBox<String> slot = new JComboBox<>(new String[]{"09:00", "09:30", "10:00", "10:30", "11:00"});
    private final JButton book = new JButton("Book");
    private final JLabel message = new JLabel(" ");
    private boolean bookingInProgress;

    public AppointmentDesk(DoctorCatalog catalog) {
        super("Outpatient Appointment Desk");
        this.catalog = catalog;
        this.service = new AppointmentService(catalog);
        JPanel form = new JPanel(new GridLayout(5, 2, 6, 6));
        form.add(new JLabel("Patient ID")); form.add(patientId);
        form.add(new JLabel("Department")); form.add(department);
        form.add(new JLabel("Doctor")); form.add(doctor);
        form.add(new JLabel("Time slot")); form.add(slot);
        form.add(new JLabel()); form.add(book);
        add(form, BorderLayout.CENTER);
        add(message, BorderLayout.SOUTH);

        for (String d : catalog.departments()) department.addItem(d);
        department.setSelectedIndex(-1);
        doctor.setSelectedIndex(-1);

        department.addActionListener(e -> { trace("department ActionEvent -> refreshDoctors"); refreshDoctors(); });
        patientId.addActionListener(e -> trace("patientId ActionEvent (Enter pressed)"));
        patientId.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyTyped(java.awt.event.KeyEvent e) { bookingInProgress = true; }
        });
        book.addActionListener(e -> { trace("book ActionEvent -> bookAppointment"); bookAppointment(); });
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowOpened(WindowEvent e) { trace("windowOpened"); }
            @Override public void windowActivated(WindowEvent e) { trace("windowActivated"); }
            @Override public void windowClosing(WindowEvent e) { trace("windowClosing"); confirmAndClose(); }
            @Override public void windowClosed(WindowEvent e) { trace("windowClosed"); }
            @Override public void windowDeactivated(WindowEvent e) { trace("windowDeactivated"); }
        });
        pack();
        trace("constructor finished");
    }

    /** (a) Refresh the doctor list when the department changes. */
    private void refreshDoctors() {
        doctor.removeAllItems();
        String dept = (String) department.getSelectedItem();
        if (dept == null) return;
        for (String name : catalog.doctorsFor(dept)) doctor.addItem(name);
        doctor.setSelectedIndex(-1);
        bookingInProgress = true;
    }

    /** (b) Validate and book. */
    private void bookAppointment() {
        try {
            message.setForeground(new Color(0, 110, 0));
            message.setText(service.book(patientId.getText(), (String) department.getSelectedItem(),
                    (String) doctor.getSelectedItem(), (String) slot.getSelectedItem()));
            bookingInProgress = false;
            patientId.setText("");
        } catch (BookingException ex) {
            message.setForeground(Color.RED);
            message.setText(ex.getMessage());
        }
    }

    private void confirmAndClose() {
        if (bookingInProgress) {
            int answer = JOptionPane.showConfirmDialog(this, "A booking is in progress. Close anyway?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (answer != JOptionPane.YES_OPTION) return;
        }
        dispose();
    }

    private static void trace(String text) { System.out.println("[" + Thread.currentThread().getName() + "] " + text); }

    public static void main(String[] args) {
        trace("main starts");
        DoctorCatalog catalog = new CachedDoctorCatalog(DoctorSource.sample());
        SwingUtilities.invokeLater(() -> new AppointmentDesk(catalog).setVisible(true));
    }
}
