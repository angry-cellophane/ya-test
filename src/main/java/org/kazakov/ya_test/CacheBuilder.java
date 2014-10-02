package org.kazakov.ya_test;

import static org.kazakov.ya_test.CacheDefaultParameterValues.MAX_SIZE;
import static org.kazakov.ya_test.CacheDefaultParameterValues.STRATEGY;

/**
 * Created by alexander on 01.10.14.
 */
public interface CacheBuilder {

    CacheBuilder maxSize(int maxSize);

    CacheBuilder useStrategy(CacheStrategy strategy);
    CacheBuilder useThreadSafe();

    <K,V> Cache<K,V> build();
}
