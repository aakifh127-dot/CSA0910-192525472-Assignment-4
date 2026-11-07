/** Headless tests: the whole payment logic is verified without opening a window. */
public class FeeServiceTest {
    static int passed, failed;

    interface Body { void run() throws Exception; }

    static void check(String name, boolean ok) {
        System.out.println((ok ? "PASS " : "FAIL ") + name);
        if (ok) passed++; else failed++;
    }

    static String message(Body b) {
        try { b.run(); return null; } catch (FeeException e) { return e.getMessage(); } catch (Exception e) { return "UNEXPECTED " + e; }
    }

    public static void main(String[] args) throws Exception {
        InMemoryFeeRepository repo = new InMemoryFeeRepository(0);
        FeeService s = new FeeService(repo);

        Receipt r = s.pay("192525472", "1500.50");
        check("T1 valid payment returns receipt #1 for 1500.50", r.number() == 1 && r.amount() == 1500.50);
        check("T2 empty register number rejected", "Register number is required".equals(message(() -> s.pay("  ", "10"))));
        check("T3 empty amount rejected", "Amount is required".equals(message(() -> s.pay("192525472", ""))));
        check("T4 non-numeric amount rejected (no NumberFormatException escapes)", "Amount must be a number".equals(message(() -> s.pay("192525472", "abc"))));
        check("T5 negative amount rejected", message(() -> s.pay("192525472", "-5")) != null);
        check("T6 zero amount rejected", message(() -> s.pay("192525472", "0")) != null);
        check("T7 NaN rejected", message(() -> s.pay("192525472", "NaN")) != null);
        check("T8 amount above limit rejected", message(() -> s.pay("192525472", "2000000")) != null);
        check("T9 SQL-injection text rejected before reaching storage", message(() -> s.pay("1'); DROP TABLE fees;--", "10")) != null);
        check("T10 nothing was stored by the rejected calls", repo.findLast("192525472").number() == 1);
        check("T11 receipt for last payment", s.lastReceipt("192525472").amount() == 1500.50);
        check("T12 receipt for unknown student reports no payment", message(() -> s.lastReceipt("999999999")) != null);
        s.pay("192525472", "200");
        check("T13 receipt returns the most recent payment", s.lastReceipt("192525472").amount() == 200);
        repo.failNextCall();
        check("T14 storage failure becomes a readable message", "Payment could not be saved. Please try again.".equals(message(() -> s.pay("192525472", "50"))));

        System.out.println(passed + " passed, " + failed + " failed");
        if (failed > 0) System.exit(1);
    }
}
