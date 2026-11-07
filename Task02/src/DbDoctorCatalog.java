import java.util.List;

/** Alternative design: ask the database every time the department changes. */
public class DbDoctorCatalog implements DoctorCatalog {
    private final DoctorSource source;
    public DbDoctorCatalog(DoctorSource source) { this.source = source; }
    @Override public List<String> departments() { return source.departments(); }
    @Override public List<String> doctorsFor(String department) { return source.query(department); }
}
