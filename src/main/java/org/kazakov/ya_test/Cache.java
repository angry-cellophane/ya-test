package org.kazakov.ya_test;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Created by alexander on 01.10.14.
 */
public interface Cache<K,V> {

    /**
     * get a value by a specified key. The key cannot be null.
     * @return the value mapped by the specified key
     */
    V get(Object key);

    /**
     * put a pair of key and value into the map.
     * if a pair with the specified key already exists, then an old value overrides with the specified value.
     * @param key cannot be null.
     * @param value
     * @return an old value if it's already been in the map else null
     */
    V put(K key, V value);
}
