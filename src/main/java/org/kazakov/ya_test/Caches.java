package org.kazakov.ya_test;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import static org.kazakov.ya_test.CacheDefaultParameterValues.IS_THREAD_SAFE;
import static org.kazakov.ya_test.CacheDefaultParameterValues.MAX_SIZE;
import static org.kazakov.ya_test.CacheDefaultParameterValues.STRATEGY;

/**
 * Created by alexander on 01.10.14.
 */
public class Caches {

    private static class Builder implements CacheBuilder {

        private int maxSize;
        private CacheStrategy cacheStrategy;
        private boolean isThreadSafe;

        private Builder() {
            this.maxSize = MAX_SIZE;
            this.cacheStrategy = STRATEGY;
            this.isThreadSafe = IS_THREAD_SAFE;
        }

        public CacheBuilder maxSize(int maxSize) {
            if (maxSize < 0) throw new IllegalArgumentException("Cache capacity should be a positive number");
            this.maxSize = maxSize;
            return this;
        }

        public CacheBuilder useStrategy(CacheStrategy strategy) {
            this.cacheStrategy = strategy;
            this.isThreadSafe = true;
            return this;
        }

        @Override
        public CacheBuilder useThreadSafe() {
            return this;
        }

        public <K,V> Cache<K,V> build(){
            if (maxSize == 1) return new SingletonCache<>();

            switch (cacheStrategy) {
                case FIFO: return new FifoCache<K, V>(maxSize);
                case LRU:
                    if (isThreadSafe)
                        return new ThreadSafeLruCache<>(maxSize);
                    else
                        return new LruCache<K, V>(maxSize);
                default: throw new IllegalArgumentException("Unknown cache strategy :" +cacheStrategy);
            }
        }
    }

    public static CacheBuilder newBuilder() {
        return new Builder();
    }

    private Caches(){}
}
