package org.kazakov.ya_test;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by alexander on 02.10.14.
 */
@ThreadSafe
public class ThreadSafeLruCache<K,V> implements Cache<K,V> {

    private final ConcurrentLinkedHashMap<K,V> map;

    ThreadSafeLruCache(int maxSize) {
//        this.map = new ConcurrentSkipListMap<K,V>() {
//
//            void afterNodeInsertion() {
//                ConcurrentSkipListMap.Entry<K,V> first;
//                if ((first = head) != null && removeEldestEntry(first)) {
//                    K key = first.key;
//                    removeNode(hash(key), key, null, false, true);
//                }
//            }
//        };
        this.map = new ConcurrentLinkedHashMap.Builder<K,V>()
                .initialCapacity(maxSize)
                .maximumWeightedCapacity(maxSize)
                .build();
    }

    @Override
    public V get(@Nonnull Object key) {
        return map.get(key);
    }

    @Override
    public V put(@Nonnull K key, @Nonnull V value) {
        return map.put(key, value);
    }
}
