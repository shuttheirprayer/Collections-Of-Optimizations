package com.misanthropy.collections_of_optimizations.core;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public final class ReusableListIterator implements Iterator<Object> {
    private static final ReusableListIterator[] SLOTS = new ReusableListIterator[256];

    private final Thread owner;
    private List<?> list;
    private int index;

    private ReusableListIterator(Thread owner) {
        this.owner = owner;
    }

    public static Iterator<?> of(List<?> list) {
        Thread thread = Thread.currentThread();
        int slot = (int) (thread.getId() & (SLOTS.length - 1));
        ReusableListIterator it = SLOTS[slot];
        if (it == null) {
            it = new ReusableListIterator(thread);
            SLOTS[slot] = it;
        } else if (it.owner != thread) {
            return list.iterator();
        }
        it.list = list;
        it.index = 0;
        return it;
    }

    @Override
    public boolean hasNext() {
        return index < list.size();
    }

    @Override
    public Object next() {
        if (index >= list.size()) {
            throw new NoSuchElementException();
        }
        return list.get(index++);
    }
}
