import java.util.HashMap;
import java.util.Map;

/**
 * Alternative concept: one listener with an if-else chain on the action command (up to k string comparisons)
 * versus one listener object per button (direct call, O(1)) versus a HashMap of handlers (O(1) expected).
 * Worst case is measured: the event comes from the LAST button.
 */
public class DispatchBench {
    static volatile int sink;

    public static void main(String[] args) {
        int[] ks = {2, 8, 32, 128, 512};
        int rounds = 2_000_000;
        System.out.printf("%-6s %-22s %-22s %-22s %-18s%n", "k", "if-else chain (ns)", "listener per button (ns)", "HashMap lookup (ns)", "k listeners (bytes)");
        for (int k : ks) {
            String[] cmds = new String[k];
            Runnable[] listeners = new Runnable[k];
            Map<String, Runnable> map = new HashMap<>();
            long before = used();
            for (int i = 0; i < k; i++) {
                final int id = i;
                cmds[i] = "Command" + i;
                listeners[i] = () -> sink += id;
            }
            long after = used();
            for (int i = 0; i < k; i++) map.put(cmds[i], listeners[i]);
            String event = new String("Command" + (k - 1));            // not interned: equals() must compare characters

            long chain = time(() -> {
                for (int i = 0; i < k; i++) if (cmds[i].equals(event)) { listeners[i].run(); break; }
            }, rounds);
            long direct = time(() -> listeners[k - 1].run(), rounds);
            long hashed = time(() -> map.get(event).run(), rounds);
            System.out.printf("%-6d %-22.1f %-22.1f %-22.1f %-18d%n", k, chain / (double) rounds, direct / (double) rounds, hashed / (double) rounds, Math.max(after - before, 0));
        }
        System.out.println("Chain cost grows with k (O(k)); a listener per button stays flat (O(1)); space is O(k) for both.");
    }

    static long time(Runnable r, int rounds) {
        for (int i = 0; i < rounds / 2; i++) r.run();                  // warm-up
        long t = System.nanoTime();
        for (int i = 0; i < rounds; i++) r.run();
        return System.nanoTime() - t;
    }

    static long used() {
        System.gc();
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }
}
