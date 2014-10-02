import org.kazakov.ya_test.Cache;
import org.kazakov.ya_test.Caches;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.kazakov.ya_test.CacheStrategy.FIFO;
import static org.kazakov.ya_test.CacheStrategy.LRU;
import static org.testng.Assert.*;

/**
 * Created by alexander on 01.10.14.
 */
@Test
public class CacheTest {

    private static final Map<Long, Long> VALUES = new LinkedHashMap<Long, Long>() {{
        put(42L, 42L);
        put(46L, 43L);
        put(43L, 143L);
        put(1L, 123L);
        put(-1L, 521213L);
    }};

    @DataProvider(name = "simple")
    public static Object[][] simpleData() {
        Cache<Long, Long> fifoCache = Caches.newBuilder()
                .useStrategy(FIFO)
                .maxSize(VALUES.size() + 1)
                .build();
        Cache<Long, Long> lruCache = Caches.newBuilder()
                .useStrategy(LRU)
                .maxSize(VALUES.size() + 1)
                .build();

        VALUES.forEach((k, v) -> {
            fifoCache.put(k, v);
            lruCache.put(k, v);
        });

        return new Object[][]{
                {VALUES, fifoCache},
                {VALUES, lruCache}
        };
    }

    @Test(dataProvider = "simple")
    public void simpleDataTest(Map<Long, Long> expectedValues, final Cache<Long, Long> cache) {
        expectedValues.forEach((k, v) -> assertEquals(cache.get(k), v));
    }

    @DataProvider(name = "itemLimit")
    public static Object[][] itemLimit() {
        int cacheSize = 3;

        final Cache<Long, Long> lruCache = Caches.newBuilder()
                .useStrategy(LRU)
                .maxSize(cacheSize)
                .build();

        final Cache<Long, Long> threadSafeLruCache = Caches.newBuilder()
                .useStrategy(LRU)
                .useThreadSafe()
                .maxSize(cacheSize)
                .build();

        return new Object[][] {
                itemLimitFifoData(),
                itemLimitLruData(lruCache, cacheSize),
                itemLimitLruData(threadSafeLruCache, cacheSize)
        };
    }

    private static Object[] itemLimitFifoData() {
        int cacheSize = 3;
        Cache<Long, Long> fifoCache = Caches.newBuilder()
                .useStrategy(FIFO)
                .maxSize(cacheSize)
                .build();

        VALUES.forEach(fifoCache::put);

        int outtaMapStartIndex = VALUES.size() - cacheSize;

        Map<Long, Long> inMapValues = new LinkedHashMap<>();
        Map<Long, Long> outtaMapValues = new LinkedHashMap<>();

        Iterator<Map.Entry<Long, Long>> it = VALUES.entrySet().iterator();
        for (int i = 0; i < VALUES.size(); i++) {
            Map.Entry<Long, Long> e = it.next();
            if (i < outtaMapStartIndex) {
                outtaMapValues.put(e.getKey(), e.getValue());
            } else {
                inMapValues.put(e.getKey(), e.getValue());
            }
        }
        return new Object[]{
                inMapValues, outtaMapValues, fifoCache
        };
    }

    private static Object[] itemLimitLruData(Cache<Long, Long> lruCache, int cacheSize) {
        Map<Long, Long> inMapValues = slice(VALUES, 0, cacheSize - 1);

        slice(VALUES, 0, cacheSize).forEach(lruCache::put);
        AtomicLong sum = new AtomicLong(0);
        inMapValues.forEach((k, v) -> sum.addAndGet(lruCache.get(k)));
        assertTrue(sum.get() > Long.MIN_VALUE);
        Map.Entry<Long, Long> e = getEntry(VALUES, cacheSize);
        lruCache.put(e.getKey(), e.getValue());
        inMapValues.put(e.getKey(), e.getValue());

        Map<Long, Long> outtaMapValues = slice(VALUES, cacheSize +1, VALUES.size());
        Map.Entry<Long, Long> removedEntry = getEntry(VALUES, cacheSize -1);
        outtaMapValues.put(removedEntry.getKey(), removedEntry.getValue());

        return new Object[]{
                inMapValues, outtaMapValues, lruCache
        };
    }

    @Test(dataProvider = "itemLimit")
    public void itemLimitTest(Map<Long, Long> inMapValues,
                                  Map<Long, Long> outtaMapValues,
                                  Cache<Long, Long> cache) {
        inMapValues.forEach((k, v) -> assertEquals(cache.get(k), v) );
        outtaMapValues.forEach( (k,v) -> assertNull(cache.get(k)) );
    }

    private static <K,V> Map<K,V> slice(Map<K,V> map, int fromEntryIndex, int toEntryIndex) {
        Map<K,V> result = new LinkedHashMap<>(toEntryIndex - fromEntryIndex);
        int i = 0;
        Iterator<Map.Entry<K, V>> it = map.entrySet().iterator();
        while (it.hasNext() && i < toEntryIndex) {
            Map.Entry<K, V> e = it.next();
            if (i >= fromEntryIndex) result.put(e.getKey(), e.getValue());
            i++;
        }
        return result;
    }

    private static <K,V> Map.Entry<K,V> getEntry(Map<K, V> map, int index) {
        int i = 0;
        for (Map.Entry<K, V> e : map.entrySet()) {
            if (i++ == index) return e;
        }
        return null;
    }
}
