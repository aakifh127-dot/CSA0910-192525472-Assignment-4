import java.util.List;

/** Headless tests for the catalog and the booking rules behind the window. */
public class AppointmentTests {
    static int passed, failed;
    static void check(String name, boolean ok) { System.out.println((ok ? "PASS " : "FAIL ") + name); if (ok) passed++; else failed++; }
    static String err(AppointmentService s, String p, String d, String doc, String slot) {
        try { s.book(p, d, doc, slot); return null; } catch (BookingException e) { return e.getMessage(); }
    }

    public static void main(String[] args) throws Exception {
        DoctorSource src = DoctorSource.sample();
        DoctorCatalog cache = new CachedDoctorCatalog(src), db = new DbDoctorCatalog(src);
        check("T1 department list has 3 departments", cache.departments().size() == 3);
        check("T2 doctors change with the department (Cardiology=2)", cache.doctorsFor("Cardiology").size() == 2);
        check("T3 doctors change with the department (Orthopaedics=3)", cache.doctorsFor("Orthopaedics").size() == 3);
        check("T4 cached and database catalogues return the same doctors", List.copyOf(cache.doctorsFor("Paediatrics")).equals(db.doctorsFor("Paediatrics")));
        check("T5 unknown department gives an empty list", cache.doctorsFor("Dermatology").isEmpty());

        AppointmentService s = new AppointmentService(cache);
        String ok = s.book("P1001", "Cardiology", "Dr. Meera Rao", "09:00");
        check("T6 valid booking confirmed", ok.startsWith("Booked: appointment A1"));
        check("T7 same doctor and slot rejected", err(s, "P1002", "Cardiology", "Dr. Meera Rao", "09:00") != null);
        check("T8 same doctor, other slot accepted", err(s, "P1002", "Cardiology", "Dr. Meera Rao", "09:30") == null);
        check("T9 empty patient id rejected", "Patient ID is required".equals(err(s, " ", "Cardiology", "Dr. Meera Rao", "10:00")));
        check("T10 no department rejected", "Select a department".equals(err(s, "P1003", null, null, "10:00")));
        check("T11 no doctor rejected", "Select a doctor".equals(err(s, "P1003", "Cardiology", null, "10:00")));
        check("T12 doctor from another department rejected", err(s, "P1003", "Cardiology", "Dr. Lakshmi Devi", "10:00") != null);
        check("T13 no slot rejected", "Select a time slot".equals(err(s, "P1003", "Cardiology", "Dr. Arun Kumar", null)));
        System.out.println(passed + " passed, " + failed + " failed");
        if (failed > 0) System.exit(1);
    }
}
