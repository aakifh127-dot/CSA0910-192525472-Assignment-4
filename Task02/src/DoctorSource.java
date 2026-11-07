import java.util.ArrayList;
import java.util.List;

/**
 * SIMULATED database table of doctors (no real DB is needed to run this task).
 * query() scans all m rows (like an unindexed SELECT) and sleeps latencyMicros to imitate a network round trip.
 */
public class DoctorSource {
    private final List<Doctor> rows = new ArrayList<>();
    private final long latencyMicros;
    public int queries;

    public DoctorSource(int departments, int doctors, long latencyMicros) {
        this.latencyMicros = latencyMicros;
        for (int i = 0; i < doctors; i++) rows.add(new Doctor("Dr. Name" + i, "Dept" + (i % departments)));
    }

    public static DoctorSource sample() {
        DoctorSource s = new DoctorSource(0, 0, 0);
        String[][] data = {
            {"Cardiology", "Dr. Meera Rao", "Dr. Arun Kumar"},
            {"Orthopaedics", "Dr. S. Iyer", "Dr. P. Nair", "Dr. Fatima Khan"},
            {"Paediatrics", "Dr. Lakshmi Devi"},
        };
        for (String[] d : data) for (int i = 1; i < d.length; i++) s.rows.add(new Doctor(d[i], d[0]));
        return s;
    }

    public synchronized List<String> query(String department) {
        queries++;
        if (latencyMicros > 0) {
            long end = System.nanoTime() + latencyMicros * 1000;
            while (System.nanoTime() < end) { /* busy wait: accurate for very short latencies */ }
        }
        List<String> out = new ArrayList<>();
        for (Doctor d : rows) if (d.department().equals(department)) out.add(d.name());
        return out;
    }

    public List<String> departments() {
        List<String> out = new ArrayList<>();
        for (Doctor d : rows) if (!out.contains(d.department())) out.add(d.department());
        return out;
    }

    public List<Doctor> all() { return rows; }
}
