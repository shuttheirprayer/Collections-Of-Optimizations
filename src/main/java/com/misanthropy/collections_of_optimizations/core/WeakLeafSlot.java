package com.misanthropy.collections_of_optimizations.core;

import java.lang.ref.WeakReference;

public final class WeakLeafSlot extends ThreadLocal<Object> {

    @Override
    public Object get() {
        Object held = super.get();
        return held == null ? null : ((WeakReference<?>) held).get();
    }

    @Override
    public void set(Object leaf) {
        Object held = super.get();
        if (held == null ? leaf == null : ((WeakReference<?>) held).get() == leaf) {
            return;
        }
        super.set(leaf == null ? null : new WeakReference<>(leaf));
    }
}
