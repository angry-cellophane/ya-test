package org.kazakov.ya_test;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by alexander on 01.10.14.
 */
@ThreadSafe
public class FifoCache<K,V> implements Cache<K,V> {

    private class Node {
        public final K key;
        public final V value;
        public volatile Node next;
        public volatile Node prev;

        private Node(K key, @Nonnull V value, Node prev, Node next) {
            this.key = key;
            this.value = value;
            this.prev = prev;
            this.next = next;
        }
    }

    private class Snapshot {
        public final Node head;
        public final Node tail;
        public final int size;

        private Snapshot(Node head, Node tail, int size) {
            this.head = head;
            this.tail = tail;
            this.size = size;
        }
    }

    private final int maxSize;
    private final ConcurrentHashMap<K,Node> map;
    private final AtomicReference<Snapshot> state;

    FifoCache(int maxSize) {
        this.map = new ConcurrentHashMap<>(maxSize);
        this.maxSize = maxSize;
        this.state = new AtomicReference<>(new Snapshot(null,null, 0));
    }

    @Override
    public V get(Object key) {
        Node node = map.get(key);
        return node == null ? null : node.value;
    }

    @Override
    public V put(K key, V value) {
        for (;;) {
            Snapshot oldState = state.get();
            Node oldHead = oldState.head;
            Node oldTail = oldState.tail;
            Node newHead = new Node(key, value, null, oldHead);

            Node oldNode;
            if (oldState.size == 0) {
                Snapshot newState = new Snapshot(newHead, newHead, 1);
                if (!state.compareAndSet(oldState, newState)) continue;
                oldNode = map.put(key, newHead);
            } else if (oldState.size < maxSize) {
                Snapshot newState = new Snapshot(newHead, oldTail, oldState.size + 1);
                if (!state.compareAndSet(oldState, newState)) continue;
                oldNode = map.put(key, newHead);
                oldHead.prev = newHead;
            } else {
                Node newTail = oldState.tail.prev;
                Snapshot newState = new Snapshot(newHead, newTail, oldState.size);
                if (!state.compareAndSet(oldState, newState)) continue;
                oldNode = map.put(key, newHead);
                newTail.next = null;
                oldHead.prev = newHead;
                map.remove(oldTail.key);
            }

            if (oldNode != null) {
                Node prev = oldNode.prev;
                Node next = oldNode.next;

                if (prev != null) {
                    prev.next = next;
                }
                if (next != null) {
                    next.prev = prev;
                }
                return oldNode.value;
            } else {
                return null;
            }
        }
    }
}
