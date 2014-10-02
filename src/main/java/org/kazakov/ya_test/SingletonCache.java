package org.kazakov.ya_test;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by alexander on 01.10.14.
 */
public class SingletonCache<K,V> implements Cache<K,V> {

    private class Pair {

        public final K key;
        public final V value;
        private Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

    }

    private final AtomicReference<Pair> entry;

    SingletonCache() {
        entry = new AtomicReference<>(null);
    }

    @Override
    public V get(@Nonnull Object key) {
        Pair pair = entry.get();
        return pair != null && pair.key.equals(key) ? pair.value : null;
    }

    @Override
    public V put(@Nonnull K key, @Nonnull V value) {
        Pair oldPair = entry.get();
        entry.set(new Pair(key, value));
        return oldPair != null && oldPair.key.equals(key) ? oldPair.value : null;
    }
}
