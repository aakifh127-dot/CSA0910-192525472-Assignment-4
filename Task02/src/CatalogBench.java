import java.util.List;

/**
 * Per-department-change time and memory: Map loaded once (cache) vs query on every change (simulated database).
 * The simulated round trip is 200 microseconds + a full scan of m rows; a real network DB is usually slower.
 */
public class CatalogBench {
    static volatile int sink;

    public static void main(String[] args) {
        int[][] sizes = {{10, 500}, {20, 2000}, {50, 10000}, {100, 50000}};   // {d departments, m doctors}
        System.out.printf("%-6s %-8s %-22s %-24s %-18s%n", "d", "m", "Map lookup (ns/change)", "DB query (ns/change)", "Map memory (KB)");
        for (int[] sz : sizes) {
            int d = sz[0], m = sz[1];
            DoctorSource src = new DoctorSource(d, m, 200);
            int copies = 40;                                   // build several copies so the average is above GC noise
            DoctorCatalog[] keep = new DoctorCatalog[copies];
            long before = used();
            for (int c = 0; c < copies; c++) keep[c] = new CachedDoctorCatalog(src);
            long mapBytes = (used() - before) / copies;
            DoctorCatalog cache = keep[0];
            DoctorCatalog db = new DbDoctorCatalog(src);
            int rounds = 200000, dbRounds = 300;
            for (int i = 0; i < rounds; i++) sink += cache.doctorsFor("Dept" + (i % d)).size();   // warm-up
            long t = System.nanoTime();
            for (int i = 0; i < rounds; i++) sink += cache.doctorsFor("Dept" + (i % d)).size();
            long cacheNs = (System.nanoTime() - t) / rounds;
            t = System.nanoTime();
            for (int i = 0; i < dbRounds; i++) sink += db.doctorsFor("Dept" + (i % d)).size();
            long dbNs = (System.nanoTime() - t) / dbRounds;
            System.out.printf("%-6d %-8d %-22d %-24d %-18.1f%n", d, m, cacheNs, dbNs, Math.max(mapBytes, 0) / 1024.0);
        }
        System.out.println("Map: O(1) per change, O(d + m) memory.  DB: O(m) scan + a round trip per change, O(m/d) memory for the current list only.");
    }

    static long used() { System.gc(); Runtime r = Runtime.getRuntime(); return r.totalMemory() - r.freeMemory(); }
}
