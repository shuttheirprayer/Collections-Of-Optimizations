package com.misanthropy.collections_of_optimizations.core;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class StringCharIterator implements Iterator<Character> {
    private final String text;
    private int index;

    public StringCharIterator(String text) {
        this.text = text;
    }

    @Override
    public boolean hasNext() {
        return index < text.length();
    }

    @Override
    public Character next() {
        if (index >= text.length()) {
            throw new NoSuchElementException();
        }
        return text.charAt(index++);
    }
}
