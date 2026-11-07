import java.util.ArrayList;
import java.util.List;

/** Test double / demo store. latencyMs imitates a slow bank so the window can show it stays responsive. */
public class InMemoryFeeRepository implements FeeRepository {
    private final List<Receipt> rows = new ArrayList<>();
    private final long latencyMs;
    private boolean failNext;

    public InMemoryFeeRepository(long latencyMs) { this.latencyMs = latencyMs; }
    public void failNextCall() { failNext = true; }

    @Override public synchronized Receipt save(String regNo, double amount) throws FeeException {
        pause();
        if (failNext) { failNext = false; throw new FeeException("Payment could not be saved. Please try again."); }
        Receipt r = new Receipt(rows.size() + 1, regNo, amount);
        rows.add(r);
        return r;
    }

    @Override public synchronized Receipt findLast(String regNo) {
        for (int i = rows.size() - 1; i >= 0; i--) if (rows.get(i).regNo().equals(regNo)) return rows.get(i);
        return null;
    }

    private void pause() {
        if (latencyMs > 0) try { Thread.sleep(latencyMs); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
