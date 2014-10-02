package org.kazakov.ya_test;

import static org.kazakov.ya_test.CacheStrategy.FIFO;

/**
 * Created by alexander on 01.10.14.
 */
class CacheDefaultParameterValues {

    static final int MAX_SIZE = 100;
    static final CacheStrategy STRATEGY = FIFO;
    static final boolean IS_THREAD_SAFE = false;

    private CacheDefaultParameterValues(){}
}
