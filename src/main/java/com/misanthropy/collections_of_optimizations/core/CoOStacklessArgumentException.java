package com.misanthropy.collections_of_optimizations.core;

public final class CoOStacklessArgumentException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    public CoOStacklessArgumentException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
