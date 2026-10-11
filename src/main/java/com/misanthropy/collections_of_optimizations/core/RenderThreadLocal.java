package com.misanthropy.collections_of_optimizations.core;

import com.mojang.blaze3d.systems.RenderSystem;

public final class RenderThreadLocal<T> extends ThreadLocal<T> {

    private final ThreadLocal<T> delegate;

    private T renderValue;

    private boolean renderValueSet;

    public RenderThreadLocal(ThreadLocal<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public T get() {
        if (RenderSystem.isOnRenderThread()) {
            if (!this.renderValueSet) {
                this.renderValue = this.delegate.get();
                this.renderValueSet = true;
            }
            return this.renderValue;
        }
        return this.delegate.get();
    }

    @Override
    public void set(T value) {
        if (RenderSystem.isOnRenderThread()) {
            this.renderValue = value;
            this.renderValueSet = true;
            return;
        }
        this.delegate.set(value);
    }

    @Override
    public void remove() {
        if (RenderSystem.isOnRenderThread()) {
            this.renderValue = null;
            this.renderValueSet = false;
            this.delegate.remove();
            return;
        }
        this.delegate.remove();
    }
}
