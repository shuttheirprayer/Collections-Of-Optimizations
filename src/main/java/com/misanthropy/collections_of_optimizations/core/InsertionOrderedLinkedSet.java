package com.misanthropy.collections_of_optimizations.core;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;

import java.util.NoSuchElementException;
import java.util.function.Consumer;

public final class InsertionOrderedLinkedSet<K> extends ObjectLinkedOpenHashSet<K> {

    private ObjectArrayList<K> order;

    public InsertionOrderedLinkedSet(int expected) {
        super(expected);
        this.order = new ObjectArrayList<>(expected);
    }

    @Override
    public boolean add(K key) {
        boolean added = super.add(key);
        ObjectArrayList<K> keys = this.order;
        if (added && keys != null) {
            keys.add(key);
        }
        return added;
    }

    @Override
    public K addOrGet(K key) {
        this.order = null;
        return super.addOrGet(key);
    }

    @Override
    public boolean remove(Object key) {
        this.order = null;
        return super.remove(key);
    }

    @Override
    public K removeFirst() {
        this.order = null;
        return super.removeFirst();
    }

    @Override
    public K removeLast() {
        this.order = null;
        return super.removeLast();
    }

    @Override
    public boolean addAndMoveToFirst(K key) {
        this.order = null;
        return super.addAndMoveToFirst(key);
    }

    @Override
    public boolean addAndMoveToLast(K key) {
        this.order = null;
        return super.addAndMoveToLast(key);
    }

    @Override
    public void clear() {
        this.order = null;
        super.clear();
    }

    @Override
    public ObjectListIterator<K> iterator() {
        ObjectArrayList<K> keys = this.order;
        if (keys == null) {
            return super.iterator();
        }
        return new OrderIterator(keys.elements(), keys.size());
    }

    @Override
    public ObjectListIterator<K> iterator(K from) {
        this.order = null;
        return super.iterator(from);
    }

    @Override
    @SuppressWarnings("unchecked")
    public InsertionOrderedLinkedSet<K> clone() {
        InsertionOrderedLinkedSet<K> copy = (InsertionOrderedLinkedSet<K>) super.clone();
        ObjectArrayList<K> keys = this.order;
        copy.order = keys == null ? null : keys.clone();
        return copy;
    }

    @Override
    public void forEach(Consumer<? super K> action) {
        ObjectArrayList<K> keys = this.order;
        if (keys == null) {
            super.forEach(action);
            return;
        }
        K[] elements = keys.elements();
        int size = keys.size();
        for (int i = 0; i < size; i++) {
            action.accept(elements[i]);
        }
    }

    private final class OrderIterator implements ObjectListIterator<K> {
        private final K[] elements;
        private final int size;
        private int next;
        private int last = -1;

        private OrderIterator(K[] elements, int size) {
            this.elements = elements;
            this.size = size;
        }

        @Override
        public boolean hasNext() {
            return this.next < this.size;
        }

        @Override
        public boolean hasPrevious() {
            return this.next > 0;
        }

        @Override
        public K next() {
            if (this.next >= this.size) {
                throw new NoSuchElementException();
            }
            this.last = this.next;
            return this.elements[this.next++];
        }

        @Override
        public K previous() {
            if (this.next <= 0) {
                throw new NoSuchElementException();
            }
            this.last = --this.next;
            return this.elements[this.next];
        }

        @Override
        public int nextIndex() {
            return this.next;
        }

        @Override
        public int previousIndex() {
            return this.next - 1;
        }

        @Override
        public void remove() {
            if (this.last < 0) {
                throw new IllegalStateException();
            }
            InsertionOrderedLinkedSet.this.remove(this.elements[this.last]);
            this.last = -1;
        }
    }
}
