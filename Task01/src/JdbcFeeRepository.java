import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * JDBC implementation. Uses PreparedStatement (no SQL injection), try-with-resources (connection always closed)
 * and converts SQLException into a user-safe FeeException.
 * Table: fees(id INT AUTO_INCREMENT PRIMARY KEY, reg_no VARCHAR(12), amount DECIMAL(10,2))
 */
public class JdbcFeeRepository implements FeeRepository {
    private final String url, user, password;

    public JdbcFeeRepository(String url, String user, String password) {
        this.url = url; this.user = user; this.password = password;
    }

    @Override public Receipt save(String regNo, double amount) throws FeeException {
        String sql = "INSERT INTO fees(reg_no, amount) VALUES (?, ?)";
        try (Connection c = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, regNo);
            ps.setDouble(2, amount);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return new Receipt(keys.getInt(1), regNo, amount);
            }
        } catch (SQLException e) {
            throw new FeeException("Payment could not be saved. Please try again.", e);
        }
    }

    @Override public Receipt findLast(String regNo) throws FeeException {
        String sql = "SELECT id, amount FROM fees WHERE reg_no = ? ORDER BY id DESC LIMIT 1";
        try (Connection c = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, regNo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? new Receipt(rs.getInt(1), regNo, rs.getDouble(2)) : null;
            }
        } catch (SQLException e) {
            throw new FeeException("Receipt could not be read. Please try again.", e);
        }
    }
}
