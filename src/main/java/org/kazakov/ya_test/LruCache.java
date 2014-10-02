package org.kazakov.ya_test;

import com.google.common.cache.*;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by alexander on 01.10.14.
 */
public class LruCache<K, V> implements Cache<K, V> {

    private final Map<K, V> map;
    private final int maxSize;

    public LruCache(int maxSize) {
        this.maxSize = maxSize;
        map = new LinkedHashMap<K,V>(maxSize, 1.0f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
                return size() > maxSize;
            }
        };
    }

    @Override
    public V get(Object key) {
        return map.get(key);
    }

    @Override
    public V put(K key, V value) {
        return map.put(key, value);
    }
}
