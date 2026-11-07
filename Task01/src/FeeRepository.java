/** Storage abstraction: the service does not know whether this is JDBC or memory. */
public interface FeeRepository {
    Receipt save(String regNo, double amount) throws FeeException;
    Receipt findLast(String regNo) throws FeeException;   // null when nothing paid yet
}
