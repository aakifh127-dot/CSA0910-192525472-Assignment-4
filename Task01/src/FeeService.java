import java.util.regex.Pattern;

/** All validation and payment rules. No AWT/Swing imports: it can be unit-tested without a window. */
public class FeeService {
    public static final double MAX_AMOUNT = 1_000_000;
    private static final Pattern REG_NO = Pattern.compile("[A-Za-z0-9]{6,12}");
    private final FeeRepository repository;

    public FeeService(FeeRepository repository) { this.repository = repository; }

    public Receipt pay(String regNo, String amountText) throws FeeException {
        String reg = checkRegNo(regNo);
        double amount = checkAmount(amountText);
        return repository.save(reg, amount);
    }

    public Receipt lastReceipt(String regNo) throws FeeException {
        Receipt r = repository.findLast(checkRegNo(regNo));
        if (r == null) throw new FeeException("No payment found for " + regNo.trim());
        return r;
    }

    static String checkRegNo(String regNo) throws FeeException {
        String r = regNo == null ? "" : regNo.trim();
        if (r.isEmpty()) throw new FeeException("Register number is required");
        if (!REG_NO.matcher(r).matches()) throw new FeeException("Register number must be 6-12 letters or digits");
        return r;
    }

    static double checkAmount(String text) throws FeeException {
        String t = text == null ? "" : text.trim();
        if (t.isEmpty()) throw new FeeException("Amount is required");
        double a;
        try { a = Double.parseDouble(t); }
        catch (NumberFormatException e) { throw new FeeException("Amount must be a number"); }
        if (Double.isNaN(a) || Double.isInfinite(a) || a <= 0) throw new FeeException("Amount must be greater than zero");
        if (a > MAX_AMOUNT) throw new FeeException("Amount cannot exceed " + (long) MAX_AMOUNT);
        return a;
    }
}
