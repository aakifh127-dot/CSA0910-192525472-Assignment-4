import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Chosen design: load once into Map<String, List<String>>; every department change is a hash lookup. */
public class CachedDoctorCatalog implements DoctorCatalog {
    private final Map<String, List<String>> byDepartment = new HashMap<>();

    public CachedDoctorCatalog(DoctorSource source) {
        for (Doctor d : source.all())
            byDepartment.computeIfAbsent(d.department(), k -> new ArrayList<>()).add(d.name());
    }

    @Override public List<String> departments() { return new ArrayList<>(byDepartment.keySet()); }
    @Override public List<String> doctorsFor(String department) { return byDepartment.getOrDefault(department, List.of()); }
}
