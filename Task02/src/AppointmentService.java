import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/** Validation and booking rules, independent of the window. */
public class AppointmentService {
    private static final Pattern PATIENT_ID = Pattern.compile("[A-Za-z0-9]{4,12}");
    private final DoctorCatalog catalog;
    private final Set<String> booked = new HashSet<>();
    private int nextId = 1;

    public AppointmentService(DoctorCatalog catalog) { this.catalog = catalog; }

    public String book(String patientId, String department, String doctor, String slot) throws BookingException {
        String id = patientId == null ? "" : patientId.trim();
        if (id.isEmpty()) throw new BookingException("Patient ID is required");
        if (!PATIENT_ID.matcher(id).matches()) throw new BookingException("Patient ID must be 4-12 letters or digits");
        if (department == null) throw new BookingException("Select a department");
        if (doctor == null) throw new BookingException("Select a doctor");
        List<String> valid = catalog.doctorsFor(department);
        if (!valid.contains(doctor)) throw new BookingException(doctor + " does not work in " + department);
        if (slot == null) throw new BookingException("Select a time slot");
        if (!booked.add(doctor + "|" + slot)) throw new BookingException(doctor + " is already booked at " + slot);
        return "Booked: appointment A" + (nextId++) + " for " + id + " with " + doctor + " at " + slot;
    }
}
