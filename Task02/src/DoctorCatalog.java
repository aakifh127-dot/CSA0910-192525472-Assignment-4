import java.util.List;

public interface DoctorCatalog {
    List<String> departments();
    List<String> doctorsFor(String department);
}
