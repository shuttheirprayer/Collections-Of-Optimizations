package com.misanthropy.collections_of_optimizations.core;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public final class GeodePoints<E> extends LinkedList<E> {
    private static final ThreadLocal<GeodePoints<?>> CENTERS = new ThreadLocal<>();

    public double outer;
    private Object[] items = new Object[16];
    private int count;
    private final Cursor cursor = new Cursor();

    public static <E> GeodePoints<E> startCenters() {
        GeodePoints<E> points = new GeodePoints<>();
        CENTERS.set(points);
        return points;
    }

    @SuppressWarnings("unchecked")
    public static <E> GeodePoints<E> centers() {
        return (GeodePoints<E>) CENTERS.get();
    }

    @Override
    public boolean add(E element) {
        if (this.count == this.items.length) {
            this.items = Arrays.copyOf(this.items, this.count * 2);
        }
        this.items[this.count++] = element;
        return super.add(element);
    }

    @Override
    @SuppressWarnings("unchecked")
    public E get(int index) {
        return this.count == this.size() && index < this.count ? (E) this.items[index] : super.get(index);
    }

    @Override
    public Iterator<E> iterator() {
        if (this.count != this.size()) {
            return super.iterator();
        }
        this.cursor.index = 0;
        return this.cursor;
    }

    private final class Cursor implements Iterator<E> {
        int index;

        @Override
        public boolean hasNext() {
            return this.index < GeodePoints.this.count;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E next() {
            if (this.index >= GeodePoints.this.count) {
                throw new NoSuchElementException();
            }
            return (E) GeodePoints.this.items[this.index++];
        }
    }
}
